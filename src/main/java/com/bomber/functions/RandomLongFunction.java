package com.bomber.functions;

import java.util.concurrent.ThreadLocalRandom;

public class RandomLongFunction implements Function {

	private final long min;
	private final long max;

	public RandomLongFunction(long min, long max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String execute() {
		return Long.toString(ThreadLocalRandom.current().nextLong(min, max + 1));
	}
}
