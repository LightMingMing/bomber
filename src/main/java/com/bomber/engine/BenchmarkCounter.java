package com.bomber.engine;

public class BenchmarkCounter extends Counter {

	public BenchmarkCounter(int start) {
		super(start);
	}

	@Override
	int getAndCount() {
		return start;
	}
}