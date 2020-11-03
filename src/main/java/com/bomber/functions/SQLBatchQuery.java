package com.bomber.functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;

@FuncInfo(requiredArgs = "url, user, password, sql, ret, count", retArg = "ret")
public class SQLBatchQuery extends AbstractSQLQuery {

	private static final int MAX_BATCH_SIZE = 1 << 10; // 1024

	private DataSource dataSource;
	private String sql;
	private String[] ret;

	private int totalQuery = 0;
	private int batchSize = 1;

	private int next = 0;
	private List<Map<String, String>> cache;

	@Override
	public void init(Input input) {
		String url = input.get("url");
		String user = input.get("user");
		String password = input.get("password");

		this.sql = input.get("sql");
		this.ret = input.get("ret").split(", *");
		this.dataSource = getDataSourceManager().getDataSource(url, user, password);
	}

	@Override
	public Map<String, String> execute(Input input) {
		if (totalQuery == 0)
			totalQuery = Integer.parseInt(input.get("count"));
		if (cache == null) {
			cache = executeBatchQuery(totalQuery++, batchSize);
		}
		if (next == batchSize) {
			if (batchSize < MAX_BATCH_SIZE) {
				batchSize <<= 1;
			}
			cache = executeBatchQuery(totalQuery, batchSize);
			totalQuery += batchSize;
			next = 0;
		}
		// Avoid IndexOutOfBoundsException
		if (next >= cache.size()) {
			next++;
			return Collections.emptyMap();
		}
		return cache.get(next++);
	}

	protected List<Map<String, String>> executeBatchQuery(Integer start, Integer batchSize) {
		String batchSQL = String.format("%s limit %d offset %d", sql, batchSize, start);
		try (Connection connection = dataSource.getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(batchSQL)) {
			return getMapFromResultSet(rs, batchSize);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected List<Map<String, String>> getMapFromResultSet(ResultSet rs, int batchSize) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int numColumns = metaData.getColumnCount();
		int minNumColumns = Math.min(numColumns, ret.length);
		List<Map<String, String>> result = new ArrayList<>(batchSize);
		while (rs.next()) {
			Map<String, String> current = new HashMap<>();
			for (int i = 0; i < minNumColumns; i++) {
				current.put(ret[i], rs.getString(i + 1));
			}
			result.add(current);
		}
		return result;
	}
}
