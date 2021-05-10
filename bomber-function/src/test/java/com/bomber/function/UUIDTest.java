package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class UUIDTest {

	@Test
	public void testExecute() {
		UUID uuid = new UUID();
		assertThat(uuid.execute()).doesNotContain("-");

		uuid = new UUID(false);
		assertThat(uuid.execute()).contains("-");
	}

}