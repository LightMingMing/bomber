package com.bomber.functions.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultDependencyHandler implements DependencyHandler {

	public static DefaultDependencyHandler DEFAULT = new DefaultDependencyHandler();

	private List<String> intersection(Collection<String> c1, Collection<String> c2) {
		return c1.stream().filter(c2::contains).collect(Collectors.toList());
	}

	@Override
	public List<FunctionContext> handle(Collection<FunctionContext> disordered) {
		Set<String> found = new HashSet<>();
		List<FunctionContext> snapshot = new ArrayList<>(disordered);
		List<FunctionContext> ordered = new ArrayList<>(disordered.size());
		while (!snapshot.isEmpty()) {
			int mark = snapshot.size();
			Iterator<FunctionContext> iterator = snapshot.iterator();
			while (iterator.hasNext()) {
				FunctionContext next = iterator.next();
				Set<String> dependentArgs;
				if ((dependentArgs = next.dependentKeys()).isEmpty() || found.containsAll(dependentArgs)) {
					ordered.add(next);
					List<String> intersection = intersection(found, next.retKeys());
					if (!intersection.isEmpty()) {
						throw new IllegalArgumentException("Duplicate return keys " + intersection);
					}
					found.addAll(next.retKeys());
					iterator.remove();
				}
			}
			if (mark == snapshot.size()) {
				throw new IllegalArgumentException("Failed to handling dependencies");
			}
		}
		return ordered;
	}

	@Override
	public List<FunctionContext> handle(Collection<FunctionContext> disordered, String... chooses) {
		List<FunctionContext> ordered = handle(disordered);
		if (chooses.length == 0) {
			return ordered;
		}
		Set<String> found = new HashSet<>();
		for (String choose : chooses) {
			find(choose, ordered, found);
		}
		return ordered.stream().filter(each -> found.contains(each.name())).collect(Collectors.toList());
	}

	@Override
	public List<FunctionContext> handle(Collection<FunctionContext> disordered, Collection<String> chooses) {
		List<FunctionContext> ordered = handle(disordered);
		if (chooses == null || chooses.isEmpty()) {
			return ordered;
		}
		Set<String> found = new HashSet<>();
		for (String choose : chooses) {
			find(choose, ordered, found);
		}
		return ordered.stream().filter(each -> found.contains(each.name())).collect(Collectors.toList());
	}

	private void find(String key, Collection<FunctionContext> all, Set<String> found) {
		FunctionContext ctx = all.stream().filter(o -> o.retKeys().contains(key)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("The key '" + key + "' can't found"));
		found.add(ctx.name());
		for (String each : ctx.dependentKeys()) {
			if (!found.contains(each)) {
				find(each, all, found);
			}
		}
	}
}
