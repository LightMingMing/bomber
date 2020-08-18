package com.bomber.functions;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionExecutorTest {

	@Test
	public void testExecute() {
		FunctionOption f1 = new FunctionOption();
		f1.setFunctionName("Sum");
		f1.setArgumentValues("a=${a}, b=${b}"); // c = a + b
		f1.setKey("c");

		FunctionOption f2 = new FunctionOption();
		f2.setFunctionName("Property");
		f2.setArgumentValues("value=100");
		f2.setKey("a");

		FunctionOption f3 = new FunctionOption();
		f3.setFunctionName("Property");
		f3.setArgumentValues("value=200");
		f3.setKey("b");

		FunctionOption f4 = new FunctionOption();
		f4.setFunctionName("Sum");
		f4.setArgumentValues("a=${a}, b=${c}"); // d = a + a + b
		f4.setKey("d");

		FunctionExecutor executor = new FunctionExecutor(Arrays.asList(f1, f2, f3, f4));
		Map<String, String> result = executor.execute();

		assertThat(result.get("a")).isEqualTo("100");
		assertThat(result.get("b")).isEqualTo("200");
		assertThat(result.get("c")).isEqualTo("300");
		assertThat(result.get("d")).isEqualTo("400");
	}

	@Test
	public void testOffsetExecute() {
		FunctionOption f1 = new FunctionOption();
		f1.setFunctionName("Sum");
		f1.setArgumentValues("a=${a}, b=${b}"); // c = a + b
		f1.setKey("c");

		FunctionOption f2 = new FunctionOption();
		f2.setFunctionName("Counter");
		f2.setKey("a");

		FunctionOption f3 = new FunctionOption();
		f3.setFunctionName("Counter");
		f3.setKey("b");

		FunctionExecutor executor = new FunctionExecutor(Arrays.asList(f1, f2, f3));
		List<Map<String, String>> result = executor.execute(100, 1);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).get("a")).isEqualTo("100");
		assertThat(result.get(0).get("b")).isEqualTo("100");
		assertThat(result.get(0).get("c")).isEqualTo("200");
	}
}