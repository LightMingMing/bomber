package com.bomber.functions;

import org.junit.Test;

import java.util.Arrays;
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
	public void testOffsetExecute() {
		FunctionOption f1 = makeOption("c", "Sum", "a=${a}, b=${b}"); // c = a + b
		FunctionOption f2 = makeOption("a", "Counter", null);
		FunctionOption f3 = makeOption("b", "Counter", null);

		FunctionExecutor executor = new FunctionExecutor(Arrays.asList(f1, f2, f3));
		List<Map<String, String>> result = executor.execute(100, 1);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).get("a")).isEqualTo("100");
		assertThat(result.get(0).get("b")).isEqualTo("100");
		assertThat(result.get(0).get("c")).isEqualTo("200");
	}
}