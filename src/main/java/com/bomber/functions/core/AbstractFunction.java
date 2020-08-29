package com.bomber.functions.core;

import java.io.IOException;

public abstract class AbstractFunction<T> implements Function<T> {
	@Override
	public void init(Input input) {

	}

	@Override
	public void jump(int steps) {

	}

	@Override
	public void close() throws IOException {

	}
}
