package com.bomber.engine.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class BenchmarkCounterTest {

	@Test
	public void testBenchmarkCounter() {
		Counter counter = new BenchmarkCounter(100);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(100);
	}
}