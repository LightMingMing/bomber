package com.bomber.function.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bomber.function.model.FunctionContext;

/**
 * 函数执行默认实现
 *
 * @author MingMing Zhao
 */
public class DefaultFunctionExecutor implements FunctionExecutor {

	private static final DependencyHandler handler = DefaultDependencyHandler.DEFAULT;

	private final Collection<FunctionContext> ordered;

	public DefaultFunctionExecutor(Collection<FunctionContext> disordered) {
		this(disordered, null);
	}

	public DefaultFunctionExecutor(Collection<FunctionContext> disordered, Collection<String> chooses) {
		this.ordered = handler.handle(disordered, chooses);
	}

	@Override
	public Map<String, String> execute() {
		Map<String, String> container = new HashMap<>();
		ordered.forEach(each -> each.fireExecute(container));
		return container;
	}

	@Override
	public void jump(int steps) {
		if (steps > 0) {
			ordered.forEach(each -> each.fireJump(steps));
		}
	}

	@Override
	public List<Map<String, String>> executeBatch(int size) {
		List<Map<String, String>> containers = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			containers.add(new HashMap<>());
		}
		for (FunctionContext ctx : ordered) {
			if (ctx.metadata().isParallel()) {
				containers.parallelStream().forEach(ctx::fireExecute);
			} else {
				containers.forEach(ctx::fireExecute);
			}
		}
		return containers;
	}

}
