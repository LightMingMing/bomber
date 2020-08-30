package com.bomber.functions;

import static org.assertj.core.api.Assertions.*;

import com.bomber.functions.core.Input;
import org.junit.Test;

public class FixedLengthRandomTest {

	@Test
	public void testFixedLength() {
		FixedLengthRandom func = new FixedLengthRandom();
		for (int i = 0; i < 10; i++) {
			int len = (i + 1) * 10;
			func.init(new Input("length", len + ""));
			assertThat(func.execute(Input.EMPTY)).hasSize(len);
		}
	}
}