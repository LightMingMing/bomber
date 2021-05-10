package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class CounterTest {

	@Test
	public void testExecute() {
		Counter counter = new Counter();

		assertThat(counter.execute()).isEqualTo("0");

		counter.jump(100);

		assertThat(counter.execute()).isEqualTo("101");
	}
}
