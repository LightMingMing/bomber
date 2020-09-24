package com.bomber.engine;

import java.util.List;

public class ThreadCounter extends ThreadGroupCounter {

	protected final List<Integer> threadGroups;
	protected int threadCount;

	ThreadCounter(int start, List<Integer> threadGroups, int cursor) {
		super(start, cursor);
		this.threadGroups = threadGroups;

		for (int i = 0; i < cursor; i++) {
			this.threadCount += threadGroups.get(cursor);
		}
	}

	@Override
	int getAndCount() {
		int result = start + threadCount;
		threadCount += threadGroups.get(cursor++);
		return result;
	}
}