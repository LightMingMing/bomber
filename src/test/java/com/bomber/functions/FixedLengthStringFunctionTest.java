package com.bomber.functions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FixedLengthStringFunctionTest extends BaseFunctionExecutor<FixedLengthStringFunction> {

	@Test
	public void testFixedLength() {
		Map<String, String> params = new HashMap<>();
		params.put("length", "1");

		Function func = newFunction(params);

		assertThat(func.execute()).isEqualTo("0");
		assertThat(func.execute()).isEqualTo("1");

		assertThat(execute(func, 100)).isEqualTo("1");

		assertThat(func.execute()).isEqualTo("2");
	}

	@Test
	public void testFixLengthWithDecorate() {
		Map<String, String> params = new HashMap<>();
		params.put("length", "20");
		params.put("prefix", "PREFIX");
		params.put("suffix", "SUFFIX");

		Function func = newFunction(params);

		execute(func, 1000);

		assertThat(func.execute()).isEqualTo("PREFIX00000000000000001000SUFFIX");
	}
}