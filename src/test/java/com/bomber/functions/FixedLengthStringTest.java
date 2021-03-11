package com.bomber.functions;

import static com.bomber.functions.FixedLengthString.MAX_LENGTH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;

import com.bomber.functions.core.Function;
import com.bomber.functions.core.Input;

public class FixedLengthStringTest {

	@Test
	public void testFixedLength() {
		Function<String> func = new FixedLengthString();
		func.init(new Input("length", "1"));

		assertThat(func.execute(Input.EMPTY)).isEqualTo("0");
		assertThat(func.execute(Input.EMPTY)).isEqualTo("1");

		for (int i = 0; i < 10; i++) {
			func.execute(Input.EMPTY);
		}
		assertThat(func.execute(Input.EMPTY)).isEqualTo("2");
	}

	@Test
	public void testFixedLengthWithDecorator() {
		Function<String> func = new FixedLengthString();
		int length = 48;
		String prefix = "FixedLength";
		func.init(new Input("length", "" + length, "prefix", prefix));

		String expected0 = prefix + "0".repeat(length - prefix.length());
		assertThat(func.execute(Input.EMPTY)).isEqualTo(expected0);

		String expected1 = prefix + "0".repeat(length - prefix.length() - 1) + "1";
		assertThat(func.execute(Input.EMPTY)).isEqualTo(expected1);
	}

	@Test
	public void testMaxLength() {
		Function<String> func = new FixedLengthString();
		func.init(new Input("length", "" + MAX_LENGTH));
		assertThat(func.execute(Input.EMPTY)).isEqualTo("0".repeat(MAX_LENGTH));
	}

}