package com.bomber.functions;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FixedLengthStringFunctionTest extends FunctionTest {

	@Test
	public void testFixedLength() {
		FixedLengthStringFunction func = new FixedLengthStringFunction(2);

		assertThat(func.execute()).isEqualTo("0");
		assertThat(func.execute()).isEqualTo("1");

		assertThat(execute(func, 100)).isEqualTo("1");

		assertThat(func.execute()).isEqualTo("2");
	}

	@Test
	public void testFixLengthWithDecorate() {
		FixedLengthStringFunction func = new FixedLengthStringFunction(5, "PREFIX", "SUFFIX");

		execute(func, 1000);

		assertThat(func.execute()).isEqualTo("PREFIX01000SUFFIX");
	}
}