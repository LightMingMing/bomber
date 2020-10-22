package com.bomber.functions.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.bomber.functions.Counter;
import com.bomber.functions.Properties;
import com.bomber.functions.RetArg;
import com.bomber.functions.Sum;

public class DefaultDependencyHandlerTest {

	private final DependencyHandler handler = DefaultDependencyHandler.DEFAULT;

	@Test
	public void testHandle() {
		Input input = new Input("a", "${a}", "b", "${b}");
		FunctionContext f1 = new DefaultFunctionContext("c", new Sum(), input);
		FunctionContext f2 = new DefaultFunctionContext("a", new Counter());
		FunctionContext f3 = new DefaultFunctionContext("b", new Properties(), "b", "100");

		List<FunctionContext> list = handler.handle(Arrays.asList(f1, f2, f3));
		assertThat(list).hasSize(3);
		assertThat(list.get(0)).isEqualTo(f2);
		assertThat(list.get(1)).isEqualTo(f3);
		assertThat(list.get(2)).isEqualTo(f1);

		list = handler.handle(Arrays.asList(f1, f2, f3), "a");
		assertThat(list).hasSize(1);

		list = handler.handle(Arrays.asList(f1, f2, f3), "b");
		assertThat(list).hasSize(1);

		list = handler.handle(Arrays.asList(f1, f2, f3), "c");
		assertThat(list).hasSize(3);
	}

	@Test
	public void testRetArg() {
		Input i1 = new Input("a", "${a}", "b", "${b}");
		FunctionContext f1 = new DefaultFunctionContext("c", new Sum(), i1);

		Input i2 = new Input("retArgs", "a, b", "value", "100");
		FunctionContext f2 = new DefaultFunctionContext("f", new RetArg(), i2);

		List<FunctionContext> list = handler.handle(Arrays.asList(f1, f2));

		assertThat(list).hasSize(2);
		assertThat(list.get(0)).isEqualTo(f2);
		assertThat(list.get(1)).isEqualTo(f1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDuplicateReturnKeys() {
		Input i1 = new Input("a", "${a}", "b", "${b}");
		FunctionContext f1 = new DefaultFunctionContext("c", new Sum(), i1);

		Input i2 = new Input("c", "${a}", "d", "${b}");
		FunctionContext f2 = new DefaultFunctionContext("properties", new Properties(), i2);

		handler.handle(Arrays.asList(f1, f2));
	}

}