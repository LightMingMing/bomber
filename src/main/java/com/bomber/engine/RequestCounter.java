package com.bomber.engine;

import java.util.List;

public class RequestCounter extends ThreadCounter {

	protected final int requestsPerThread;

	RequestCounter(int start, List<Integer> threadGroups, int cursor, int requestsPerThread) {
		super(start, threadGroups, cursor);
		this.requestsPerThread = requestsPerThread;
	}

	@Override
	int getAndCount() {
		int result = start + threadCount * requestsPerThread;
		threadCount += threadGroups.get(cursor++);
		return result;
	}
}