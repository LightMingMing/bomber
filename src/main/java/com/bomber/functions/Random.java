package com.bomber.functions;

import java.util.concurrent.ThreadLocalRandom;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "min, max")
public class Random extends StringFunction {

	private long min;
	private long max;

	@Override
	public void init(Input input) {
		this.min = Long.parseLong(input.get("min"));
		this.max = Long.parseLong(input.get("max"));
	}

	@Override
	public String execute(Input input) {
		return Long.toString(ThreadLocalRandom.current().nextLong(min, max));
	}
}
