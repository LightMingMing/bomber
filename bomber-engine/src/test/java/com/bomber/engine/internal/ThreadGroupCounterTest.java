package com.bomber.engine.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class ThreadGroupCounterTest {

	@Test
	public void testThreadGroupCounter() {
		Counter counter = new ThreadGroupCounter(100, 0);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(101);
		assertThat(counter.getAndCount()).isEqualTo(102);
	}
}