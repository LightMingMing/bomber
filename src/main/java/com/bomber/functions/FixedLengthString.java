package com.bomber.functions;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "length", optionalArgs = "prefix, suffix")
public class FixedLengthString extends StringFunction {

	protected static final int MAX_LENGTH = 64;

	protected static final int MAX_COUNT_LENGTH = 18;

	private int length;

	private String prefix;

	private String suffix;

	private int numberLength;

	private long mod;

	private long count;

	private static int stringSize(long x) {
		long p = 10;
		for (int i = 1; i < MAX_COUNT_LENGTH; i++) {
			if (x < p)
				return i;
			p = 10 * p;
		}
		return MAX_COUNT_LENGTH;
	}

	@Override
	public void init(Input input) {
		int length = Integer.parseInt(input.get("length"));
		if (length < 1 || length > MAX_LENGTH) {
			throw new IllegalArgumentException("length: " + length + ", (expected: 1-" + MAX_LENGTH + ")");
		}
		this.length = length;

		this.prefix = input.get("prefix");
		this.suffix = input.get("suffix");

		int numberLength = length;
		if (prefix != null) {
			numberLength -= prefix.length();
		}
		if (suffix != null) {
			numberLength -= suffix.length();
		}
		if (numberLength < 1) {
			throw new IllegalArgumentException("numberLength: " + numberLength + ", (expected: > 0)");
		}
		this.numberLength = numberLength;
		this.mod = (long) Math.pow(10, Math.min(this.numberLength, MAX_COUNT_LENGTH));
	}

	@Override
	public String execute(Input input) {
		StringBuilder sb = new StringBuilder(length);

		if (prefix != null)
			sb.append(prefix);

		int paddedZeroLength = numberLength - stringSize(count);
		if (paddedZeroLength > 0) {
			sb.append("0".repeat(paddedZeroLength));
		}

		sb.append(count);

		if (suffix != null)
			sb.append(suffix);

		count = (++count) % mod;
		return sb.toString();
	}

	@Override
	public void jump(int steps) {
		count = (count + steps) % mod;
	}
}
