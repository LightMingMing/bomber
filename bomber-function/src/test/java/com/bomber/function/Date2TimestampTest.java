package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class Date2TimestampTest {

	@Test
	public void testConvert() throws ParseException {
		String date = "2021-05-10 17:30:00";

		String timestamp = new Date2Timestamp().execute(date);

		assertThat(new Timestamp2Date().execute(timestamp)).isEqualTo(date);
	}
}