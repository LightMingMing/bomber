package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class FixedLengthRandomTest {

	@Test
	public void testFixedLengthRandom() {
		for (int i = 10; i < FixedLengthRandom.MAX_LENGTH; i += 10) {
			assertThat(new FixedLengthRandom(i).execute()).hasSize(i);
		}
	}
}