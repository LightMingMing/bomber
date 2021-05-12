package com.bomber.function.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class DefaultFunctionContextTest {

	@Test
	public void testFunctionContext() {
		FunctionContext f1 = new DefaultFunctionContext("c", "Counter");
		FunctionContext f2 = new DefaultFunctionContext("sum", "MVEL",
				Map.of("script", "(int)c + 100", "args", "c"));

		Map<String, String> container = new HashMap<>();
		f1.fireExecute(container);
		f2.fireExecute(container);
		assertThat(container).containsEntry("sum", "100");

		f1.fireJump(100);
		f2.fireJump(100);
		container = new HashMap<>();
		f1.fireExecute(container);
		f2.fireExecute(container);
		assertThat(container).containsEntry("sum", "201");
	}
}