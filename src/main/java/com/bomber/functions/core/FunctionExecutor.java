package com.bomber.functions.core;

import java.util.List;
import java.util.Map;

public interface FunctionExecutor {

	Map<String, String> execute();

	List<Map<String, String>> execute(int offset, int limit);

	default List<Map<String, String>> execute(int limit) {
		return execute(0, limit);
	}

	void shutdown();
}
