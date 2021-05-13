package com.bomber.engine.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class RequestCounterTest {

	@Test
	public void testRequestCounter() {
		Counter counter = new RequestCounter(100, Arrays.asList(1, 2, 4, 8), 0, 10);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(110);
		assertThat(counter.getAndCount()).isEqualTo(130);
		assertThat(counter.getAndCount()).isEqualTo(170);
	}
}