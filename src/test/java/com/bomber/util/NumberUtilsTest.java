package com.bomber.util;

import static com.bomber.util.NumberUtils.reserveUpMaxBit;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class NumberUtilsTest {

	@Test
	public void test() {
		assertThat(reserveUpMaxBit(0), is(0.0));

		assertThat(reserveUpMaxBit(0.1), is(0.1));
		assertThat(reserveUpMaxBit(0.11), is(0.2));
		assertThat(reserveUpMaxBit(0.19), is(0.2));

		assertThat(reserveUpMaxBit(1), is(1.0));
		assertThat(reserveUpMaxBit(10), is(10.0));

		assertThat(reserveUpMaxBit(10.01), is(20.0));
		assertThat(reserveUpMaxBit(20), is(20.0));

		assertThat(reserveUpMaxBit(20.01), is(30.0));
		assertThat(reserveUpMaxBit(30), is(30.0));

		assertThat(reserveUpMaxBit(101), is(200.0));
	}

}