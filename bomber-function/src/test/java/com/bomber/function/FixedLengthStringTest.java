package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class FixedLengthStringTest {

	@Test
	public void testFixedLength() {
		FixedLengthString fixedLength = new FixedLengthString(1);

		assertThat(fixedLength.execute()).isEqualTo("0");
		assertThat(fixedLength.execute()).isEqualTo("1");

		fixedLength.jump(10);

		assertThat(fixedLength.execute()).isEqualTo("2");
	}

	@Test
	public void testFixedLengthWithPrefix() {
		int length = 48;
		String prefix = "FixedLength";
		FixedLengthString fixedLength = new FixedLengthString(length, "FixedLength");

		String expected0 = prefix + "0".repeat(length - prefix.length());
		assertThat(fixedLength.execute()).isEqualTo(expected0);

		String expected1 = prefix + "0".repeat(length - prefix.length() - 1) + "1";
		assertThat(fixedLength.execute()).isEqualTo(expected1);
	}

	@Test
	public void testMaxLength() {
		assertThat(new FixedLengthString(FixedLengthString.MAX_LENGTH).execute()).isEqualTo("0".repeat(FixedLengthString.MAX_LENGTH));
	}
}