package com.bomber.function;

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

/**
 * SQL 批量查询
 *
 * @author MingMing Zhao
 */
@FuncInfo(requiredArgs = "url, user, password, sql, ret", retArg = "ret")
public class SQLBatchQuery extends AbstractSQLQuery implements Producer<Map<String, String>> {

	protected static final int MAX_BATCH_SIZE = 1 << 10; // 1024

	private int totalQuery = 0;
	private int batchSize = 1;

	private int next = 0;
	private List<Map<String, String>> cache;

	public SQLBatchQuery(String url, String user, String password, String sql, String ret) {
		super(url, user, password, sql, ret);
	}

	@Override
	public Map<String, String> execute() {
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

	@Override
	public void jump(int steps) {
		// reset status
		this.batchSize = 1;
		this.next = 0;
		this.cache = null;
		this.totalQuery = steps;
	}

	protected List<Map<String, String>> executeBatchQuery(Integer start, Integer batchSize) {
		String batchSQL = String.format("%s limit %d offset %d", sql, batchSize, start);
		try (Connection connection = getDataSource().getConnection();
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
