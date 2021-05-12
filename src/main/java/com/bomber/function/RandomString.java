package com.bomber.function;

import org.ironrhino.core.util.CodecUtils;

@FuncInfo(requiredArgs = "length")
public class RandomString implements Producer<String> {

	private final int length;

	public RandomString(int length) {
		this.length = length;
	}

	@Override
	public String execute() {
		return CodecUtils.nextId(length);
	}

}