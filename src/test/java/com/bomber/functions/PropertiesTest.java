package com.bomber.functions;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import com.bomber.functions.core.Input;
import org.junit.Test;

public class PropertiesTest {

	@Test
	public void testExecute() {
		Input input = new Input("key", "value", "name", "bomber");

		Properties properties = new Properties();
		properties.init(input);
		Map<String, String> result = properties.execute(input);

		assertThat(result).containsEntry("key", "value");
		assertThat(result).containsEntry("name", "bomber");
	}
}