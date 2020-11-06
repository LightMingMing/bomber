package com.bomber.functions.core;

import java.util.List;
import java.util.Map;

public interface FunctionExecutor {

	void jump(int steps);

	Map<String, String> execute();

	List<Map<String, String>> execute(int count);

	void shutdown();
}
