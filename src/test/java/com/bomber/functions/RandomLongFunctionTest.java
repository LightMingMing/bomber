package com.bomber.functions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RandomLongFunctionTest extends BaseFunctionExecutor<RandomLongFunction> {

	@Test
	public void testRandomLong() {
		long min = Integer.MAX_VALUE;
		long max = min + 100;

		Map<String, String> params = new HashMap<>();
		params.put("min", "" + min);
		params.put("max", "" + max);

		Function func = newFunction(params);
		for (int i = 0; i < 100; i++) {
			assertThat(Long.parseLong(func.execute())).isBetween(min, max);
		}
	}
}