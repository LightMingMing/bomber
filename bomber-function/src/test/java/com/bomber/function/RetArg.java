package com.bomber.function;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.NonNull;

@FuncInfo(requiredArgs = "retArgs, value", retArg = "retArgs")
public class RetArg implements Function {

	public Map<String, String> execute(String retArgs, String value) {
		String[] argsToReturn = retArgs.split(", *");
		Map<String, String> ret = new HashMap<>();
		for (String each : argsToReturn) {
			ret.put(each, value);
		}
		return ret;
	}

	@Override
	public Object[] getParameterValues(@NonNull Map<String, String> initParameterValues,
									   @NonNull Map<String, String> container) {
		return replace(initParameterValues, container, "retArgs", "value");
	}
}
