package com.bomber.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;

@FuncInfo(requiredArgs = "url, user, password, sql, ret", optionalArgs = "args, argTypes", retArg = "ret", parallel = true)
public class SQLMultiRowsQuery extends AbstractSQLQuery {

	private DataSource dataSource;

	private String[] ret;

	private static String[] nullSafeSplit(String toSplit) {
		if (toSplit == null || toSplit.isEmpty()) {
			return new String[0];
		}
		return toSplit.split(", *");
	}

	@Override
	public void init(Input input) {
		this.ret = input.get("ret").split(", *");

		String url = input.get("url");
		String user = input.get("user");
		String password = input.get("password");

		this.dataSource = getDataSourceManager().getDataSource(url, user, password);
	}

	@Override
	public Map<String, String> execute(Input input) {
		String sql = input.get("sql");

		String[] args = nullSafeSplit(input.get("args"));
		String[] argsTypes = nullSafeSplit(input.get("argTypes"));
		if (args.length != argsTypes.length) {
			throw new IllegalArgumentException(
					"number of args '" + args.length + "' and number of types '" + argsTypes.length + "' not equal");
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {
			setArguments(stmt, args, argsTypes);
			try (ResultSet rs = stmt.executeQuery()) {
				return getMapFromResultSet(rs);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> getMapFromResultSet(ResultSet rs) throws SQLException {
		int columnCount = Math.min(rs.getMetaData().getColumnCount(), ret.length);

		Map<String, String> result = new HashMap<>();

		int rowCount = 0;
		while (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				result.put(ret[i - 1] + '_' + rowCount++, rs.getString(i));
			}
		}

		for (int i = 1; i <= columnCount; i++) {
			result.put(ret[i - 1] + "_n", rowCount + "");
		}

		return result;
	}

}
