package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class PropertiesTest {

	@Test
	public void testExecute() {
		Properties properties = new Properties(Map.of("name", "MingMing"));
		assertThat(properties.execute()).containsEntry("name", "MingMing");
	}
}