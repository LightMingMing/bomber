package com.bomber.functions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bomber.functions.core.Input;
import org.junit.Test;

public class FixedLengthStringTest {

	@Test
	public void testFixedLength() {
		FixedLengthString func = new FixedLengthString();
		func.init(new Input("length", "1"));

		assertThat(func.execute(Input.EMPTY)).isEqualTo("0");
		assertThat(func.execute(Input.EMPTY)).isEqualTo("1");

		for (int i = 0; i < 10; i++) {
			func.execute(Input.EMPTY);
		}
		assertThat(func.execute(Input.EMPTY)).isEqualTo("2");
	}

}