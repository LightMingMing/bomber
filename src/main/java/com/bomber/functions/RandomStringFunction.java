package com.bomber.functions;

import org.ironrhino.core.util.CodecUtils;

public class RandomStringFunction implements Function {

	private final int length;

	public RandomStringFunction(int length) {
		this.length = length;
	}

	@Override
	public String execute() {
		return CodecUtils.nextId(length);
	}
}