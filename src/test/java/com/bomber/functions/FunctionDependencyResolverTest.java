package com.bomber.functions;

import static com.bomber.functions.FunctionDependencyResolver.resolveDependencyBySorting;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class FunctionDependencyResolverTest {

	private static Map<String, String> convertToMap(String args) {
		if (args == null)
			return null;
		Map<String, String> params = new HashMap<>();
		String[] pairs = args.split(", *");
		for (String each : pairs) {
			String[] pair = each.split("=", 2);
			params.put(pair[0], pair[1]);
		}
		return params;
	}

	protected static FunctionOption makeOption(String key) {
		return makeOption(key, null, null);
	}

	protected static FunctionOption makeOption(String key, String params) {
		return makeOption(key, null, params);
	}

	protected static FunctionOption makeOption(String key, String functionName, String params) {
		FunctionOption option = new FunctionOption();
		option.setKey(key);
		option.setFunctionName(functionName);
		option.setParams(convertToMap(params));
		return option;
	}

	@Test
	public void testResolveDependencyBySorting() {
		FunctionOption f1 = makeOption("sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("a");
		FunctionOption f3 = makeOption("c", "a=${sum}, b=${a}");
		FunctionOption f4 = makeOption("b");

		List<FunctionOption> sortedOption = resolveDependencyBySorting(Arrays.asList(f1, f2, f3, f4));

		assertThat(sortedOption).hasSize(4);
		assertThat(sortedOption.get(0)).isEqualTo(f2);
		assertThat(sortedOption.get(1)).isEqualTo(f4);
		assertThat(sortedOption.get(2)).isEqualTo(f1);
		assertThat(sortedOption.get(3)).isEqualTo(f3);
	}

	@Test
	public void testCircularDependency() {
		FunctionOption f1 = makeOption("a", "b=${b}");
		FunctionOption f2 = makeOption("b", "a=${a}");
		assertThatIllegalArgumentException().isThrownBy(() -> resolveDependencyBySorting(Arrays.asList(f1, f2)));
	}

	@Test
	public void testDependentOptions() {
		FunctionOption f1 = makeOption("sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("a");
		FunctionOption f3 = makeOption("c", "a=${sum}, b=${a}");
		FunctionOption f4 = makeOption("b");

		FunctionDependencyResolver resolver = new FunctionDependencyResolver(Arrays.asList(f1, f2, f3, f4));
		List<FunctionOption> dependentOptions;

		dependentOptions = resolver.getDependentOptions(Collections.singleton("a"));
		assertThat(dependentOptions).hasSize(1); // a

		dependentOptions = resolver.getDependentOptions(Collections.singleton("sum"));
		assertThat(dependentOptions).hasSize(3); // a, b, sum

		dependentOptions = resolver.getDependentOptions(Collections.singleton("c"));
		assertThat(dependentOptions).hasSize(4); // a, b, sum, c

	}

}