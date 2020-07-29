package com.bomber.functions;

import java.util.concurrent.ThreadLocalRandom;

public class RandomIntegerFunction implements Function {

	private final int min;
	private final int max;

	public RandomIntegerFunction(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String execute() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(min, max + 1));
	}
}
