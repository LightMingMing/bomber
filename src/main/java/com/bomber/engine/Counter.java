package com.bomber.engine;

public abstract class Counter {

	protected final int start;

	Counter(int start) {
		this.start = start;
	}

	abstract int getAndCount();
}