package com.bomber.functions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RandomIntegerFunctionTest extends BaseFunctionExecutor<RandomIntegerFunction> {

	@Test
	public void testRandomInteger() {
		int min = 0;
		int max = 100;

		Map<String, String> params = new HashMap<>();
		params.put("min", "" + min);
		params.put("max", "" + max);

		Function func = newFunction(params);
		for (int i = 0; i < 100; i++) {
			assertThat(Integer.parseInt(func.execute())).isBetween(min, max);
		}
	}
}