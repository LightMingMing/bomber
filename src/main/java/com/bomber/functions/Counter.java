package com.bomber.functions;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo
public class Counter extends StringFunction {

	private int counter = 0;

	@Override
	public String execute(Input ctx) {
		return (counter++) + "";
	}

	@Override
	public void jump(int steps) {
		this.counter += steps;
	}
}
