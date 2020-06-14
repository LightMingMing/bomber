package com.bomber.util;

import java.math.BigDecimal;

public class NumberUtils {

	public static double reserveUpMaxBit(double num) {
		if (num < 0) {
			throw new IllegalArgumentException("num should not less than 0");
		}

		BigDecimal unit = new BigDecimal(1);

		int maxBitNum;

		if (num == 0) {
			return 0;
		}

		BigDecimal ten = new BigDecimal(10);
		while (num > 1) {
			if (num <= 10) {
				maxBitNum = (int) num + (num == (int) num ? 0 : 1);
				return unit.multiply(new BigDecimal(maxBitNum)).doubleValue();
			}
			num /= 10;
			unit = unit.multiply(ten);
		}

		BigDecimal oneOverTen = new BigDecimal("0.1");
		while (num < 1) {
			num *= 10;
			unit = unit.multiply(oneOverTen);
			if (num >= 1) {
				maxBitNum = (int) num + (num == (int) num ? 0 : 1);
				return unit.multiply(new BigDecimal(maxBitNum)).doubleValue();
			}
		}
		return num;
	}
}
