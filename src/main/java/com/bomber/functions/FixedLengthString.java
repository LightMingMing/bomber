package com.bomber.functions;

import com.bomber.functions.core.Input;
import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "length", optionalArgs = "prefix, suffix")
public class FixedLengthString extends StringFunction {

	protected static final int MAX_LENGTH = 18;

	private String format;

	private long mod;

	private long count;

	@Override
	public void init(Input input) {
		int length = Integer.parseInt(input.get("length"));
		if (length < 1) {
			throw new IllegalArgumentException("length should great than 0");
		}

		String prefix = input.get("prefix");
		String suffix = input.get("suffix");

		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}
		sb.append("%0").append(length).append("d");
		if (suffix != null) {
			sb.append(suffix);
		}
		this.format = sb.toString();

		this.mod = (long) Math.pow(10, Math.min(length, MAX_LENGTH));
	}

	@Override
	public String execute(Input input) {
		String result = String.format(format, count);
		count = (++count) % mod;
		return result;
	}

	@Override
	public void jump(int steps) {
		count = (count + steps) % mod;
	}
}
