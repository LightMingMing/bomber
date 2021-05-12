package com.bomber.function.runner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bomber.function.model.FunctionContext;

/**
 * 函数依赖处理器
 *
 * @author MingMing Zhao
 */
public interface DependencyHandler {

	List<FunctionContext> handle(Collection<FunctionContext> disordered);

	default List<FunctionContext> handle(Collection<FunctionContext> disordered, String... chooses) {
		return handle(disordered, Arrays.asList(chooses));
	}

	List<FunctionContext> handle(Collection<FunctionContext> disordered, Collection<String> chooses);
}
