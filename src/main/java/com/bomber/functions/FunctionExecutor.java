package com.bomber.functions;

import static com.bomber.util.ValueReplacer.getKeys;
import static com.bomber.util.ValueReplacer.replace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionExecutor {

	private final List<FunctionOption> options;

	private final Map<String, Function> singletonFunctions = new HashMap<>();

	private final List<FunctionOption> prototypeFunctionOptions = new ArrayList<>();

	public FunctionExecutor(Collection<FunctionOption> options) {
		this(options, null);
	}

	public FunctionExecutor(Collection<FunctionOption> options, Collection<String> specificFunctions) {
		FunctionDependencyResolver resolver = new FunctionDependencyResolver(options);
		this.options = specificFunctions == null ? resolver.getResolvedOptions()
				: resolver.getDependentOptions(specificFunctions);
		for (FunctionOption option : this.options) {
			String args = option.getArgumentValues();
			if (args == null || getKeys(args).isEmpty()) {
				singletonFunctions.put(option.getKey(), create(option.getFunctionName(), option.getArgumentValues()));
			} else {
				prototypeFunctionOptions.add(option);
			}
		}
	}

	public Map<String, String> execute() {
		Map<String, String> context = new HashMap<>(options.size());
		singletonFunctions.forEach((name, func) -> context.put(name, func.execute()));
		for (FunctionOption option : prototypeFunctionOptions) {
			String argumentValues = replace(option.getArgumentValues(), context);
			Function function = create(option.getFunctionName(), argumentValues);
			context.put(option.getKey(), function.execute());
		}
		return context;
	}

	public List<Map<String, String>> execute(int offset, int limit) {
		List<Map<String, String>> list = new ArrayList<>(limit);
		if (offset > 0) {
			singletonFunctions.forEach((name, func) -> func.skip(offset));
		}
		for (int i = 0; i < limit; i++) {
			list.add(execute());
		}
		return list;
	}

	private Function create(String name, String argumentValues) {
		try {
			return FunctionHelper.instance(name, argumentValues);
		} catch (IllegalAccessException | InstantiationException e) {
			throw new IllegalStateException("Failed to instance the function '" + name + "'", e);
		}
	}
}
