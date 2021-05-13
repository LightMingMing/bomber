package com.bomber.engine.internal;

import java.util.List;

/**
 * 线程计数
 *
 * @author MingMing Zhao
 */
public class ThreadCounter extends ThreadGroupCounter {

	protected final List<Integer> threadGroups;
	protected int threadCount;

	public ThreadCounter(int start, List<Integer> threadGroups, int cursor) {
		super(start, cursor);
		this.threadGroups = threadGroups;

		for (int i = 0; i < cursor; i++) {
			this.threadCount += threadGroups.get(i);
		}
	}

	@Override
	public int getAndCount() {
		int result = start + threadCount;
		threadCount += threadGroups.get(cursor++);
		return result;
	}
}