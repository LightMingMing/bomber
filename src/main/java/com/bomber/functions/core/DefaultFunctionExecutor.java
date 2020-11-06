package com.bomber.functions.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultFunctionExecutor implements FunctionExecutor {

	private final Collection<FunctionContext> ordered;

	public DefaultFunctionExecutor(Collection<FunctionContext> disordered) {
		this(disordered, null);
	}

	public DefaultFunctionExecutor(Collection<FunctionContext> disordered, Collection<String> chooses) {
		this.ordered = DefaultDependencyHandler.DEFAULT.handle(disordered, chooses);
		init();
	}

	protected void init() {
		ordered.forEach(FunctionContext::fireInit);
	}

	@Override
	public Map<String, String> execute() {
		Output output = new Output();
		ordered.forEach(each -> each.fireExecute(output));
		return output.getAll();
	}

	@Override
	public void jump(int steps) {
		if (steps > 0) {
			ordered.forEach(each -> each.fireJump(steps));
		}
	}

	@Override
	public List<Map<String, String>> execute(int count) {
		Output[] outputs = new Output[count];
		for (int i = 0; i < count; i++) {
			outputs[i] = new Output();
		}
		for (FunctionContext ctx : ordered) {
			if (ctx.metadata().isParallel()) {
				Arrays.stream(outputs).parallel().forEach(ctx::fireExecute);
			} else {
				Arrays.stream(outputs).forEach(ctx::fireExecute);
			}
		}
		List<Map<String, String>> result = new ArrayList<>();
		for (Output output : outputs) {
			result.add(output.getAll());
		}
		return result;
	}

	@Override
	public void shutdown() {
		ordered.forEach(FunctionContext::fireClose);
	}

}
