package com.bomber.functions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

public class CitizenIdentificationFunction extends AbstractStringFunction {

	private static final String[] provinces = new String[] { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32",
			"33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
			"63", "64", "65", "71", "81", "82", "91" };
	private static final int[] power = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final int MAX_SEQ = 1000; // exclusive

	private String addressCode;
	private LocalDate date;
	private int seq; // [0, 999]

	private static boolean isValid(String addressCode) {
		if (addressCode == null || addressCode.length() != 6) {
			return false;
		}
		for (int i = 0; i < 6; i++) {
			char c = addressCode.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return Arrays.binarySearch(provinces, addressCode.substring(0, 2)) > -1;
	}

	private static char getCheckBit(int sum) {
		int modulus = sum % 11;
		return modulus == 0 ? '1' : (modulus == 1 ? '0' : (modulus == 2 ? 'X' : (char) (48 + (12 - modulus))));
	}

	private static int getPowerSum(char[] bits) {
		int sum = 0;

		for (int i = 0; i < bits.length; ++i) {
			int bit = bits[i] - 48;
			sum += bit * power[i];
		}

		return sum;
	}

	@Override
	public String execute() {
		String result = addressCode + dateFormat.format(date) + String.format("%03d", seq);
		result += getCheckBit(getPowerSum(result.toCharArray()));
		if (++seq == MAX_SEQ) {
			date = date.plusDays(1);
			seq = 0;
		}
		return result;
	}

	@Override
	public void skip(int steps) {
		if (steps < 0) {
			throw new IllegalArgumentException("the steps to skipping should grater than -1");
		}
		int days = steps / MAX_SEQ;
		if ((seq += steps % MAX_SEQ) >= MAX_SEQ) {
			days += 1;
			seq -= MAX_SEQ;
		}
		date = date.plusDays(days);
	}

	@Override
	public String getRequiredArgs() {
		return "addressCode";
	}

	@Override
	public String getOptionalArgs() {
		return "startDate";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		String addressCode = params.get("addressCode");
		if (!isValid(addressCode)) {
			throw new IllegalArgumentException("Invalid addressCode '" + addressCode + "'");
		}
		String startDate = params.getOrDefault("startDate", "19700101");
		this.addressCode = addressCode;
		this.date = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
		this.seq = 0;
	}
}
