package com.bomber.functions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UUIDFunctionTest {

	@Test
	public void test() throws InstantiationException, IllegalAccessException {
		Map<String, String> params = new HashMap<>();

		params.put("noHyphen", "true");
		Function<?> func = FunctionHelper.instance("UUID", params);
		assertThat((String) func.execute()).doesNotContain("-");

		params.put("noHyphen", "false");
		func = FunctionHelper.instance("UUID", params);
		assertThat((String) func.execute()).contains("-");
	}
}