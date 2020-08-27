package com.bomber.functions;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bomber.functions.FunctionDependencyResolverTest.makeOption;
import static org.assertj.core.api.Assertions.assertThat;

public class FunctionExecutorTest {

	@Test
	public void testExecute() {
		FunctionOption f1 = makeOption("c", "Sum", "a=${a}, b=${b}"); // c = a + b
		FunctionOption f2 = makeOption("a", "Property", "value=100");
		FunctionOption f3 = makeOption("b", "Property", "value=200");
		FunctionOption f4 = makeOption("d", "Sum", "a=${a}, b=${c}"); // d = a + a + b

		FunctionExecutor executor = new FunctionExecutor(Arrays.asList(f1, f2, f3, f4));
		Map<String, String> result = executor.execute();

		assertThat(result.get("a")).isEqualTo("100");
		assertThat(result.get("b")).isEqualTo("200");
		assertThat(result.get("c")).isEqualTo("300");
		assertThat(result.get("d")).isEqualTo("400");
	}

	@Test
	public void testComplexFunctionExecute() {
		FunctionOption f1 = makeOption("s1", "Sum", "a=${a}, b=${b}");
		FunctionOption f2 = makeOption("s2", "Sum", "a=${b}, b=${c}");
		FunctionOption f3 = makeOption("s3", "Sum", "a=${c}, b=${d}");
		FunctionOption f4 = makeOption("f5", "RetInputArgNames", "a=50, b=100, c=0, d=0");

		Map<String, String> params = new HashMap<>();
		params.put("value", "200");
		params.put("retArgs", "c,d");
		FunctionOption f5 = makeOption("f6", "RetInputArgValues", params);

		FunctionExecutor executor = new FunctionExecutor(Arrays.asList(f1, f2, f3, f4, f5));
		Map<String, String> result = executor.execute();

		assertThat(result.get("s1")).isEqualTo("150");
		assertThat(result.get("s2")).isEqualTo("300");
		assertThat(result.get("s3")).isEqualTo("400");
	}

	@Test
	public void testOffsetExecute() {
		FunctionOption f1 = makeOption("c", "Sum", "a=${a}, b=${b}"); // c = a + b
		FunctionOption f2 = makeOption("a", "Counter", (String) null);
		FunctionOption f3 = makeOption("b", "Counter", (String) null);

		FunctionExecutor executor = new FunctionExecutor(Arrays.asList(f1, f2, f3));
		List<Map<String, String>> result = executor.execute(100, 1);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).get("a")).isEqualTo("100");
		assertThat(result.get(0).get("b")).isEqualTo("100");
		assertThat(result.get(0).get("c")).isEqualTo("200");
	}
}