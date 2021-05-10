package com.bomber.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.NonNull;

/**
 * SQL 单行查询
 *
 * @author MingMing Zhao
 */
@FuncInfo(requiredArgs = "url, user, password, sql, ret", optionalArgs = "args, argTypes", retArg = "ret", parallel = true)
public class SQLQuery extends AbstractSQLQuery {

	public SQLQuery(String url, String user, String password, String sql, String ret) {
		super(url, user, password, sql, ret);
	}

	private static String[] nullSafeSplit(String toSplit) {
		if (toSplit == null || toSplit.isEmpty()) {
			return new String[0];
		}
		return toSplit.split(", *");
	}

	public Map<String, String> execute(String args, String argsTypes) {
		String[] argArray = nullSafeSplit(args);
		String[] typeArray = nullSafeSplit(argsTypes);

		if (argArray.length != typeArray.length) {
			throw new IllegalArgumentException(
					"number of args '" + argArray.length + "' and number of types '" + typeArray.length + "' not equal");
		}
		try (Connection connection = getDataSource().getConnection();
			 PreparedStatement stmt = connection.prepareStatement(sql)) {
			setArguments(stmt, argArray, typeArray);
			try (ResultSet rs = stmt.executeQuery()) {
				return getMapFromResultSet(rs);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private PreparedStatement getPreparedStatement(Connection connection, String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	protected Map<String, String> getMapFromResultSet(ResultSet rs) throws SQLException {
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

	@Override
	public Object[] getParameterValues(@NonNull Map<String, String> initParameterValues,
									   @NonNull Map<String, String> container) {
		return replace(initParameterValues, container, "args", "argsTypes");
	}
}
