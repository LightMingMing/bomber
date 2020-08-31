package com.bomber.functions;

import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

import java.io.IOException;

public class Bad extends StringFunction {

	@Override
	public String execute(Input input) {
		return "Bad";
	}

	@Override
	public void close() throws IOException {
		throw new IOException("Dummy");
	}
}
