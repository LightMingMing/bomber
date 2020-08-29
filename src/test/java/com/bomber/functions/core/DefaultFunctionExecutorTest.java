package com.bomber.functions.core;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bomber.functions.Counter;
import com.bomber.functions.Properties;
import com.bomber.functions.Sum;
import org.junit.Test;

public class DefaultFunctionExecutorTest {

	@Test
	public void testExecute() throws IOException {
		Sum sum = spy(new Sum());
		FunctionContext f1 = new DefaultFunctionContext("c", sum, "a", "${a}", "b", "${b}");
		FunctionContext f2 = new DefaultFunctionContext("a", new Counter());
		FunctionContext f3 = new DefaultFunctionContext("b", new Properties(), "b", "100");

		FunctionExecutor executor = new DefaultFunctionExecutor(Arrays.asList(f1, f2, f3));

		assertThat(executor.execute().get("c")).isEqualTo("100");
		assertThat(executor.execute().get("c")).isEqualTo("101");

		then(sum).should().init(any());
		then(sum).should(times(2)).execute(any());

		executor.shutdown();
		then(sum).should().close();
	}

	@Test
	public void testJump() throws IOException {
		Sum sum = spy(new Sum());
		FunctionContext f1 = new DefaultFunctionContext("c", sum, "a", "${a}", "b", "${b}");
		FunctionContext f2 = new DefaultFunctionContext("a", new Counter());
		FunctionContext f3 = new DefaultFunctionContext("b", new Properties(), "b", "100");

		FunctionExecutor executor = new DefaultFunctionExecutor(Arrays.asList(f1, f2, f3));
		List<Map<String, String>> list = executor.execute(100, 2);

		assertThat(list).hasSize(2);
		assertThat(list.get(0)).containsEntry("c", "200");
		assertThat(list.get(1)).containsEntry("c", "201");

		then(sum).should().init(any());
		then(sum).should().jump(100);

		executor.shutdown();
		then(sum).should().close();
	}
}