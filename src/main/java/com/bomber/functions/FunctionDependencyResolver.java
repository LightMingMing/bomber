package com.bomber.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FunctionDependencyResolver {

	private final List<FunctionOption> sortedOptions;

	private final Map<String, FunctionOption> optionMap;

	public FunctionDependencyResolver(Collection<FunctionOption> original) {
		this.sortedOptions = resolveDependencyBySorting(original);
		this.optionMap = new HashMap<>(sortedOptions.size());
		for (FunctionOption option : sortedOptions) {
			optionMap.put(option.getKey(), option);
		}
	}

	public static List<FunctionOption> resolveDependencyBySorting(Collection<FunctionOption> original) {
		Set<String> ctx = new HashSet<>();
		List<FunctionOption> options = new ArrayList<>(original);
		List<FunctionOption> result = new ArrayList<>(original.size());
		while (!options.isEmpty()) {
			int mark = options.size();
			Iterator<FunctionOption> iterator = options.iterator();
			while (iterator.hasNext()) {
				FunctionOption next = iterator.next();
				Set<String> dependentArgs;
				if ((dependentArgs = next.getDependentKeys()).isEmpty() || ctx.containsAll(dependentArgs)) {
					result.add(next);
					ctx.addAll(next.getOutputKeys());
					iterator.remove();
				}
			}
			if (mark == options.size()) {
				throw new IllegalArgumentException("can't resolve dependency");
			}
		}
		return result;
	}

	private void resolveDependency(String key, Set<String> ctx) {
		FunctionOption option = optionMap.values().stream().filter(o -> o.getOutputKeys().contains(key)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Key '" + key + "' can't found"));
		ctx.add(option.getKey());
		for (String arg : option.getDependentKeys()) {
			if (!ctx.contains(arg)) {
				resolveDependency(arg, ctx);
			}
		}
	}

	public List<FunctionOption> getResolvedOptions() {
		return this.sortedOptions;
	}

	public List<FunctionOption> getDependentOptions(Collection<String> specificFunctions) {
		Set<String> ctx = new HashSet<>();
		for (String name : specificFunctions) {
			resolveDependency(name, ctx);
		}
		return sortedOptions.stream().filter(option -> ctx.contains(option.getKey())).collect(Collectors.toList());
	}
}
