package com.bomber.function;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数
 *
 * @author MingMing Zhao
 */
@FuncInfo(requiredArgs = "min, max")
public class Random implements Producer<String> {

	private final long min;
	private final long max;

	public Random(long min, long max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String execute() {
		return Long.toString(ThreadLocalRandom.current().nextLong(min, max));
	}
}
