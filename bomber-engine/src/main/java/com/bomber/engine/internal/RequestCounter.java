package com.bomber.engine.internal;

import java.util.List;

/**
 * 请求计数
 *
 * @author MingMing Zhao
 */
public class RequestCounter extends ThreadCounter {

	protected final int requestsPerThread;

	public RequestCounter(int start, List<Integer> threadGroups, int cursor, int requestsPerThread) {
		super(start, threadGroups, cursor);
		this.requestsPerThread = requestsPerThread;
	}

	@Override
	public int getAndCount() {
		int result = start + threadCount * requestsPerThread;
		threadCount += threadGroups.get(cursor++);
		return result;
	}
}