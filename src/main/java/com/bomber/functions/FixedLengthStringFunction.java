package com.bomber.functions;

import java.util.Map;

public class FixedLengthStringFunction extends AbstractFunction {

	private static final int MAX_MOD_LENGTH = 10;

	private String format;
	private long mod;

	private long count;

	@Override
	public String getRequiredArgs() {
		return "length";
	}

	@Override
	public String getOptionalArgs() {
		return "prefix, suffix";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		int length = Integer.parseInt(params.get("length"));
		if (length < 1) {
			throw new IllegalArgumentException("length should great than 0");
		}

		String prefix = params.get("prefix");
		String suffix = params.get("suffix");

		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}
		sb.append("%0").append(length).append("d");
		if (suffix != null) {
			sb.append(suffix);
		}
		this.format = sb.toString();

		this.mod = (long) Math.pow(10, Math.min(length, MAX_MOD_LENGTH));
	}

	@Override
	public String execute() {
		String result = String.format(format, count);
		count = (++count) % mod;
		return result;
	}

}
