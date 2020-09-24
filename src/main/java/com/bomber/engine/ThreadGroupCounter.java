package com.bomber.engine;

public class ThreadGroupCounter extends BenchmarkCounter {

	protected int cursor;

	ThreadGroupCounter(int start, int cursor) {
		super(start);
		this.cursor = cursor;
	}

	@Override
	int getAndCount() {
		return start + cursor++;
	}

}