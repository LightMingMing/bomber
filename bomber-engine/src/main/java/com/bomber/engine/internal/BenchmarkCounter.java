package com.bomber.engine.internal;

/**
 * Benchmark 计数
 *
 * @author MingMing Zhao
 */
public class BenchmarkCounter implements Counter {

	protected final int start;

	public BenchmarkCounter(int start) {
		this.start = start;
	}

	@Override
	public int getAndCount() {
		return start;
	}
}