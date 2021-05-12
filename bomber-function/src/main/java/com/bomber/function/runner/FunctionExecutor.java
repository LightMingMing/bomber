package com.bomber.function.runner;

import java.util.List;
import java.util.Map;

/**
 * 函数执行
 *
 * @author MingMing Zhao
 */
public interface FunctionExecutor {

	void jump(int steps);

	Map<String, String> execute();

	List<Map<String, String>> executeBatch(int size);

	default Map<String, String> execute(int offset) {
		jump(offset);
		return execute();
	}

	default List<Map<String, String>> executeBatch(int offset, int size) {
		jump(offset);
		return executeBatch(size);
	}
}
