package com.bomber.functions;

import org.ironrhino.core.util.CodecUtils;

import com.bomber.function.FuncInfo;
import com.bomber.function.Producer;

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