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
public class SQLQueryTest {

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
	public void testQuery() {
		Map<String, String> params = new HashMap<>(mysqlConfig());
		params.put("sql", "select id, createDate from user where username='admin'");
		params.put("ret", "id, createDate");

		Input input = new Input(params);
		try (SQLQuery query = new SQLQuery()) {
			query.init(input);

			long startTime = System.nanoTime();
			int count = 10000;
			for (int i = 0; i < count; i++) {
				query.execute(input);
			}
			long endTime = System.nanoTime();
			System.out.println("Select " + count + " times took " + (endTime - startTime) / 1_000_000 + "ms");
		}
	}

	@Test
	public void testPreparedQuery() {
		Map<String, String> params = new HashMap<>(mysqlConfig());
		params.put("sql", "select id, createDate from user where username=?");
		params.put("args", "admin");
		params.put("argTypes", "varchar");
		params.put("ret", "id, createDate");

		Input input = new Input(params);
		try (SQLQuery query = new SQLQuery()) {
			query.init(input);

			long startTime = System.nanoTime();
			int count = 10000;
			for (int i = 0; i < count; i++) {
				query.execute(input);
			}
			long endTime = System.nanoTime();
			System.out.println("Prepared Select " + count + " times took " + (endTime - startTime) / 1_000_000 + "ms");
		}
	}
}