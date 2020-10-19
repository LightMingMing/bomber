package com.bomber.engine;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.Test;

public class ThreadCounterTest {

	@Test
	public void testThreadCount() {
		Counter counter = new ThreadCounter(1, Arrays.asList(1, 3, 5, 7), 3);
		assertThat(counter.getAndCount()).isEqualTo(10);
	}
}