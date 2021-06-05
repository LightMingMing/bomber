package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class RandomStringTest {

	@Test
	public void testRandomString() {
		RandomString random = new RandomString(10);
		assertThat(random.execute()).hasSize(10);
	}
}