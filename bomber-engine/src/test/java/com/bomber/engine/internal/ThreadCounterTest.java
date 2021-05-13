package com.bomber.engine.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class ThreadCounterTest {

	@Test
	public void testThreadCounter() {
		Counter counter = new ThreadCounter(100, Arrays.asList(1, 2, 4, 8), 0);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(101);
		assertThat(counter.getAndCount()).isEqualTo(103);
		assertThat(counter.getAndCount()).isEqualTo(107);
	}
}