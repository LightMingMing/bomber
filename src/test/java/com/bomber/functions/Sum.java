package com.bomber.functions;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "a, b")
public class Sum extends StringFunction {

	@Override
	public String execute(Input input) {
		long a = Long.parseLong(input.get("a"));
		long b = Long.parseLong(input.get("b"));
		return a + b + "";
	}
}
