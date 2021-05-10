package com.bomber.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL 多行查询
 *
 * @author MingMing Zhao
 */
@FuncInfo(requiredArgs = "url, user, password, sql, ret", optionalArgs = "args, argTypes", retArg = "ret", parallel = true)
public class SQLMultiRowsQuery extends SQLQuery {

	public SQLMultiRowsQuery(String url, String user, String password, String sql, String ret) {
		super(url, user, password, sql, ret);
	}

	protected Map<String, String> getMapFromResultSet(ResultSet rs) throws SQLException {
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
