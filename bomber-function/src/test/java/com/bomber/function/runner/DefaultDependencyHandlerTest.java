package com.bomber.function.runner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.bomber.function.model.DefaultFunctionContext;
import com.bomber.function.model.FunctionContext;

/**
 * @author MingMing Zhao
 */
class DefaultDependencyHandlerTest {

	private final DependencyHandler handler = DefaultDependencyHandler.DEFAULT;

	@Test
	public void testDuplicateReturnKeys() {
		Map<String, String> parameter1 = Map.of("a", "${a}", "b", "${b}");
		FunctionContext f1 = new DefaultFunctionContext("c", "Sum", parameter1);

		Map<String, String> parameter2 = Map.of("c", "10", "a", "20", "b", "30");
		FunctionContext f2 = new DefaultFunctionContext("properties", "Properties", parameter2);

		assertThatIllegalArgumentException().isThrownBy(() -> handler.handle(Arrays.asList(f1, f2)))
				.withMessageStartingWith("Duplicate");
	}

	@Test
	public void testRetArg() {
		Map<String, String> parameter1 = Map.of("a", "${a}", "b", "${b}");
		FunctionContext f1 = new DefaultFunctionContext("c", "Sum", parameter1);

		Map<String, String> parameter2 = Map.of("retArgs", "a, b", "value", "100");
		FunctionContext f2 = new DefaultFunctionContext("f", "RetArg", parameter2);

		List<FunctionContext> list = handler.handle(Arrays.asList(f1, f2));

		assertThat(list).hasSize(2);
		assertThat(list.get(0)).isEqualTo(f2);
		assertThat(list.get(1)).isEqualTo(f1);
	}

	@Test
	public void testCustomArgs() {
		Map<String, String> parameter1 = Map.of("script", "(int)a+(int)b", "args", "a, b");
		FunctionContext f1 = new DefaultFunctionContext("f1", "MVEL", parameter1);

		Map<String, String> parameter2 = Map.of("a", "1", "b", "2");
		FunctionContext f2 = new DefaultFunctionContext("properties", "Properties", parameter2);

		List<FunctionContext> list = handler.handle(Arrays.asList(f1, f2));
		assertThat(list).hasSize(2);
		assertThat(list.get(0)).isEqualTo(f2);
		assertThat(list.get(1)).isEqualTo(f1);
	}

}