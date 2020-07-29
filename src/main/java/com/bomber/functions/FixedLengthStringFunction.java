package com.bomber.functions;

public class FixedLengthStringFunction implements Function {

	private static final int MIN_LENGTH = 1;
	private static final int MAX_LENGTH = 10;

	private final String prefix;
	private final String suffix;
	private final String format;
	private final long mod;

	private long count;

	public FixedLengthStringFunction(int length) {
		this(length, "", "");
	}

	public FixedLengthStringFunction(int length, String prefix) {
		this(length, prefix, "");
	}

	public FixedLengthStringFunction(int length, String prefix, String suffix) {
		length = Math.min(length, MAX_LENGTH);
		length = Math.max(length, MIN_LENGTH);
		this.prefix = prefix == null ? "" : prefix;
		this.suffix = suffix == null ? "" : suffix;
		this.mod = (long) Math.pow(10, length);
		this.format = "%s%0" + length + "d%s";
	}

	@Override
	public String execute() {
		String result = String.format(format, prefix, count, suffix);
		count = (++count) % mod;
		return result;
	}
}
