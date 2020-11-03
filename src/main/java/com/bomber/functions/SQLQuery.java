package com.bomber.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;

@FuncInfo(requiredArgs = "url, user, password, sql, ret", optionalArgs = "args, argTypes", retArg = "ret", parallel = true)
public class SQLQuery extends AbstractSQLQuery {

	private DataSource dataSource;

	private QueryType queryType;

	private String sql;

	private String[] ret;

	@Override
	public void init(Input input) {
		String url = input.get("url");
		String user = input.get("user");
		String password = input.get("password");

		this.dataSource = getDataSourceManager().getDataSource(url, user, password);

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
		dataSource = null;
	}

	private PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
		// TODO cache preparedStatement ?
		return connection.prepareStatement(sql);
	}

	private Map<String, String> getMapFromResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int numColumns = metaData.getColumnCount();
		int minNumColumns = Math.min(numColumns, ret.length);
		// only first row in SQLQuery
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
