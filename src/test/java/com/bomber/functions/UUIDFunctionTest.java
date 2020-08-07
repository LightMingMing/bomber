package com.bomber.functions;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UUIDFunctionTest {

	@Test
	public void test() throws InstantiationException, IllegalAccessException {
		String params = "noHyphen=true";
		Function func = FunctionMetadataHelper.instance("UUID", params);
		assertThat(func.execute()).doesNotContain("-");

		params = "noHyphen=false";
		func = FunctionMetadataHelper.instance("UUID", params);
		assertThat(func.execute()).contains("-");
	}
}