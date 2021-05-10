package com.bomber.function;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;

import com.bomber.function.sql.CachedDataSourceManager;
import com.bomber.function.sql.DataSourceManager;

public abstract class AbstractSQLQuery implements Function {

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

	protected final String url;

	protected final String user;

	protected final String password;

	protected final String sql;

	protected final String[] ret;

	private DataSource dataSource;

	public AbstractSQLQuery(String url, String user, String password, String sql, String ret) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.sql = Objects.requireNonNull(sql, "sql");
		this.ret = Objects.requireNonNull(ret, "ret").split(", *");
	}

	public static int getJdbcType(String name) {
		Objects.requireNonNull(name, "name");
		Integer type = jdbcTypeForName.get(name.toUpperCase());
		if (type == null) {
			throw new IllegalArgumentException("Invalid jdbc type '" + name + "'");
		}
		return type;
	}

	protected DataSourceManager getDataSourceManager() {
		return CachedDataSourceManager.INSTANCE;
	}

	protected DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = getDataSourceManager().getDataSource(url, user, password);
		}
		return dataSource;
	}

	protected void setArguments(PreparedStatement pstmt, String[] arguments, String[] types) throws SQLException {
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

}
