package com.bomber.functions.core;

import static org.assertj.core.api.Assertions.*;

import com.bomber.functions.Counter;
import org.junit.Test;

import com.bomber.functions.Properties;

public class DefaultFunctionContextTest {

	@Test
	public void testConstructor() {
		FunctionContext counter = new DefaultFunctionContext("c", new Counter());
		assertThat(counter.name()).isEqualTo("c");
		assertThat(counter.retKeys()).contains("c");
		assertThat(counter.dependentKeys()).isEmpty();

		Input input = new Input("key", "value", "name", "${app.name}");
		FunctionContext properties = new DefaultFunctionContext("p", new Properties(), input);
		assertThat(properties.name()).isEqualTo("p");
		assertThat(properties.retKeys()).containsOnly("key", "name");
		assertThat(properties.dependentKeys()).containsOnly("app.name");
	}

	@Test
	public void testExecute() {
		Output output = new Output();

		FunctionContext ctx = new DefaultFunctionContext("c", new Counter());

		ctx.fireExecute(output);

		assertThat(output.get("c")).isEqualTo("0");

		ctx.fireJump(100);

		ctx.fireExecute(output);
		assertThat(output.get("c")).isEqualTo("101");
	}
}