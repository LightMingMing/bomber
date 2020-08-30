package com.bomber.functions;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.bomber.functions.core.Input;

@Ignore
public class SQLQueryTest {

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