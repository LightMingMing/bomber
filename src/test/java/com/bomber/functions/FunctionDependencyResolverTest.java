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
			return Collections.emptyMap();
		Map<String, String> params = new HashMap<>();
		String[] pairs = args.split(", *");
		for (String each : pairs) {
			String[] pair = each.split("=", 2);
			params.put(pair[0], pair[1]);
		}
		return params;
	}

	protected static FunctionOption makeOption(String key, String functionName) {
		return makeOption(key, functionName, Collections.emptyMap());
	}

	protected static FunctionOption makeOption(String key, String functionName, String params) {
		return new FunctionOption(key, functionName, convertToMap(params));
	}

	protected static FunctionOption makeOption(String key, String functionName, Map<String, String> params) {
		return new FunctionOption(key, functionName, params);
	}

	@Test
	public void testResolveDependencyBySorting() {
		FunctionOption f1 = makeOption("sum", "Sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("a", "Counter");
		FunctionOption f3 = makeOption("c", "Sum", "a=${sum}, b=${a}");
		FunctionOption f4 = makeOption("b", "Counter");

		List<FunctionOption> sortedOption = resolveDependencyBySorting(Arrays.asList(f1, f2, f3, f4));

		assertThat(sortedOption).hasSize(4);
		assertThat(sortedOption.get(0)).isEqualTo(f2);
		assertThat(sortedOption.get(1)).isEqualTo(f4);
		assertThat(sortedOption.get(2)).isEqualTo(f1);
		assertThat(sortedOption.get(3)).isEqualTo(f3);
	}

	@Test
	public void testCircularDependency() {
		FunctionOption f1 = makeOption("a", "Counter", "b=${b}");
		FunctionOption f2 = makeOption("b", "Counter", "a=${a}");
		assertThatIllegalArgumentException().isThrownBy(() -> resolveDependencyBySorting(Arrays.asList(f1, f2)));
	}

	@Test
	public void testDependentOptions() {
		FunctionOption f1 = makeOption("sum", "Sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("a", "Counter");
		FunctionOption f3 = makeOption("c", "Sum", "a=${sum}, b=${a}");
		FunctionOption f4 = makeOption("b", "Counter");

		FunctionDependencyResolver resolver = new FunctionDependencyResolver(Arrays.asList(f1, f2, f3, f4));
		List<FunctionOption> dependentOptions;

		dependentOptions = resolver.getDependentOptions(Collections.singleton("a"));
		assertThat(dependentOptions).hasSize(1); // a

		dependentOptions = resolver.getDependentOptions(Collections.singleton("sum"));
		assertThat(dependentOptions).hasSize(3); // a, b, sum

		dependentOptions = resolver.getDependentOptions(Collections.singleton("c"));
		assertThat(dependentOptions).hasSize(4); // a, b, sum, c
	}

	@Test
	public void testResolveAllInputAsOutput() {
		FunctionOption f1 = makeOption("sum", "Sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("", "Properties", "a=1, b=2");

		FunctionDependencyResolver resolver = new FunctionDependencyResolver(Arrays.asList(f1, f2));
		List<FunctionOption> options = resolver.getResolvedOptions();

		assertThat(options).hasSize(2);
		assertThat(options.get(0)).isEqualTo(f2);
		assertThat(options.get(1)).isEqualTo(f1);
	}

	@Test
	public void testResolveRetInputArgNames() {
		FunctionOption f1 = makeOption("sum", "Sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("", "RetInputArgNames", "a=1, b=2, c=3, d=4");

		FunctionDependencyResolver resolver = new FunctionDependencyResolver(Arrays.asList(f1, f2));
		List<FunctionOption> options = resolver.getResolvedOptions();

		assertThat(options).hasSize(2);
		assertThat(options.get(0)).isEqualTo(f2);
		assertThat(options.get(1)).isEqualTo(f1);
	}

	@Test
	public void testResolveRetInputArgValues() {
		FunctionOption f1 = makeOption("sum", "Sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("", "RetInputArgValues", Collections.singletonMap("retArgs", "a, b"));

		FunctionDependencyResolver resolver = new FunctionDependencyResolver(Arrays.asList(f1, f2));
		List<FunctionOption> options = resolver.getResolvedOptions();

		assertThat(options).hasSize(2);
		assertThat(options.get(0)).isEqualTo(f2);
		assertThat(options.get(1)).isEqualTo(f1);
	}

}