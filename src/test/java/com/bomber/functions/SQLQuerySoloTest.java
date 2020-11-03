package com.bomber.functions;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bomber.functions.core.Input;
import com.bomber.sql.CachedDataSourceManager;
import com.bomber.util.MockApplicationInitializer;

@Ignore
@WebAppConfiguration
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CachedDataSourceManager.class)
public class SQLQuerySoloTest {

	@Autowired
	private ApplicationContext ctx;

	@Before
	public void setup() {
		MockApplicationInitializer.setApplicationContext(ctx);
	}

	public Map<String, String> mysqlConfig() {
		Map<String, String> params = new HashMap<>();
		params.put("url", "jdbc:mysql://localhost/bomber?characterEncoding=UTF-8&useSSL=true");
		params.put("user", "root");
		params.put("password", "123456");
		return params;
	}

	@Test
	public void testBatchQuery() {
		Map<String, String> params = new HashMap<>(mysqlConfig());
		params.put("sql", "select id from summary_report");
		params.put("ret", "id");
		params.put("count", "1000");
		Input input = new Input(params);

		int count = 1000;
		SQLBatchQuery query = new SQLBatchQuery();
		query.init(input);

		long startTime = System.nanoTime();

		for (int i = 0; i < count; i++) {
			query.execute(input);
		}
		long endTime = System.nanoTime();
		System.out.println("Batch query " + count + " rows took " + (endTime - startTime) / 1_000_000 + "ms");
	}

	@Test
	public void testQuery() {
		Map<String, String> params = new HashMap<>(mysqlConfig());
		params.put("sql", "select id from summary_report");
		params.put("ret", "id");
		params.put("argTypes", "integer");
		Input input = new Input(params);
		int start = 1000;
		int count = 1000;
		try (SQLQuery query = new SQLQuery()) {
			query.init(input);

			long startTime = System.nanoTime();
			for (int i = 0; i < count; i++) {
				params.put("args", (i + start) + "");
				query.execute(new Input(params));
			}
			long endTime = System.nanoTime();
			System.out.println("Single query " + count + " rows took " + (endTime - startTime) / 1_000_000 + "ms");
		}
	}
}

// The table size is 7600 rows
// Single query 1000 rows took 9559ms
// Batch query 1000 rows took 15ms