package com.bomber.function.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.bomber.function.Counter;
import com.bomber.function.MVEL;

/**
 * @author MingMing Zhao
 */
class MethodInvokerTest {

	@Test
	public void testProducer() {
		Counter counter = new Counter();
		MethodInvoker invoker = new MethodInvoker(Counter.class);
		assertThat(invoker.invokeMethod(counter, Map.of(), Map.of())).isEqualTo("0");
		assertThat(invoker.invokeMethod(counter, Map.of(), Map.of())).isEqualTo("1");
	}

	@Test
	public void testMvel() {
		String script = "(int)a+(int)b";
		Map<String, String> initParameterValues = Map.of("args", "a,b", "script", script);
		Map<String, String> container = Map.of("a", "3", "b", "4");
		MethodInvoker invoker = new MethodInvoker(MVEL.class);
		assertThat(invoker.invokeMethod(new MVEL(), initParameterValues, container)).isEqualTo("7");
	}

}
