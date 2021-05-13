package com.bomber.engine.internal;

/**
 * 线程组计数
 *
 * @author MingMing Zhao
 */
public class ThreadGroupCounter extends BenchmarkCounter {

	protected int cursor;

	public ThreadGroupCounter(int start, int cursor) {
		super(start);
		this.cursor = cursor;
	}

	@Override
	public int getAndCount() {
		return start + cursor++;
	}

}