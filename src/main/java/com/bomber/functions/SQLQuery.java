package com.bomber.functions;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.MapFunction;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@FuncInfo(requiredArgs = "url, user, password, sql, ret", optionalArgs = "args, argTypes", retArg = "ret", parallel = true)
public class SQLQuery extends MapFunction {

	private static final Map<String, Integer> jdbcTypeForName;

	static {
		jdbcTypeForName = new HashMap<>();
		Field[] fields = Types.class.getFields();
		for (Field field : fields) {
			try {
				jdbcTypeForName.put(field.getName(), field.getInt(null));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private DataSource dataSource;

	private QueryType queryType;

	private String sql;

	private String[] ret;

	public static int getJdbcType(String name) {
		Objects.requireNonNull(name, "name");
		Integer type = jdbcTypeForName.get(name.toUpperCase());
		if (type == null) {
			throw new IllegalArgumentException("Invalid jdbc type '" + name + "'");
		}
		return type;
	}

	@Override
	public void init(Input input) {
		String url = input.get("url");
		String user = input.get("user");
		String password = input.get("password");

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(password);
		this.dataSource = new HikariDataSource(config);

		String sql = input.get("sql");
		if (sql.contains("?")) {
			this.queryType = QueryType.PREPARED_SELECT;
		} else {
			this.queryType = QueryType.SELECT;
		}
		this.ret = input.get("ret").split(", *");
	}

	@Override
	public Map<String, String> execute(Input input) {
		this.sql = input.get("sql");
		if (queryType == QueryType.SELECT) {
			try (Connection connection = dataSource.getConnection();
					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sql)) {
				return getMapFromResultSet(rs);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else {
			String[] args = input.get("args").split(", *");
			String[] argsTypes = input.get("argTypes").split(", *");
			if (args.length != argsTypes.length) {
				throw new IllegalArgumentException("number of args '" + args.length + "' and number of types '"
						+ argsTypes.length + "' not equal");
			}
			try (Connection connection = dataSource.getConnection();
					PreparedStatement stmt = getPreparedStatement(connection)) {
				setArguments(stmt, args, argsTypes);
				try (ResultSet rs = stmt.executeQuery()) {
					return getMapFromResultSet(rs);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void close() {
		if (dataSource != null) {
			((HikariDataSource) dataSource).close();
		}
	}

	private PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
		// TODO cache preparedStatement ?
		return connection.prepareStatement(sql);
	}

	private void setArguments(PreparedStatement pstmt, String[] arguments, String[] types) throws SQLException {
		for (int i = 0; i < arguments.length; i++) {
			String argument = arguments[i];
			int type = getJdbcType(types[i]);
			setArgument(pstmt, argument, type, i + 1);
		}
	}

	// From org.apache.jmeter.protocol.jdbc.AbstractJDBCTestElement
	private void setArgument(PreparedStatement pstmt, String argument, int targetSqlType, int index)
			throws SQLException {
		switch (targetSqlType) {
		case Types.INTEGER:
			pstmt.setInt(index, Integer.parseInt(argument));
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			pstmt.setBigDecimal(index, new BigDecimal(argument));
			break;
		case Types.DOUBLE:
		case Types.FLOAT:
			pstmt.setDouble(index, Double.parseDouble(argument));
			break;
		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
			pstmt.setString(index, argument);
			break;
		case Types.BIT:
		case Types.BOOLEAN:
			pstmt.setBoolean(index, Boolean.parseBoolean(argument));
			break;
		case Types.BIGINT:
			pstmt.setLong(index, Long.parseLong(argument));
			break;
		case Types.DATE:
			pstmt.setDate(index, Date.valueOf(argument));
			break;
		case Types.REAL:
			pstmt.setFloat(index, Float.parseFloat(argument));
			break;
		case Types.TINYINT:
			pstmt.setByte(index, Byte.parseByte(argument));
			break;
		case Types.SMALLINT:
			pstmt.setShort(index, Short.parseShort(argument));
			break;
		case Types.TIMESTAMP:
			pstmt.setTimestamp(index, Timestamp.valueOf(argument));
			break;
		case Types.TIME:
			pstmt.setTime(index, Time.valueOf(argument));
			break;
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			pstmt.setBytes(index, argument.getBytes());
			break;
		case Types.NULL:
			pstmt.setNull(index, targetSqlType);
			break;
		default:
			pstmt.setObject(index, argument, targetSqlType);
		}
	}

	private Map<String, String> getMapFromResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int numColumns = metaData.getColumnCount();
		int minNumColumns = Math.min(numColumns, ret.length);
		// TODO only first row ?
		if (rs.next()) {
			Map<String, String> result = new HashMap<>();
			for (int i = 1; i <= minNumColumns; i++) {
				result.put(ret[i - 1], rs.getString(i));
			}
			return result;
		}
		return Collections.emptyMap();
	}

	enum QueryType {
		SELECT, PREPARED_SELECT
	}
}
