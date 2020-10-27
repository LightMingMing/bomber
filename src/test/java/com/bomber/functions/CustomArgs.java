package com.bomber.functions;

import java.util.Objects;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "args", customArg = "args")
public class CustomArgs extends StringFunction {

	@Override
	public String execute(Input input) {
		for (String key : input.get("args").split(", *")) {
			Objects.requireNonNull(input.get(key), key);
		}
		return "";
	}
}
