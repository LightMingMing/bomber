package com.bomber.functions;

import java.util.HashMap;
import java.util.Map;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.MapFunction;

@FuncInfo(requiredArgs = "retArgs, value", retArg = "retArgs")
public class RetArg extends MapFunction {

	@Override
	public Map<String, String> execute(Input ctx) {
		String[] retArgs = ctx.get("retArgs").split(", *");
		String value = ctx.get("value");
		Map<String, String> ret = new HashMap<>();
		for (String each : retArgs) {
			ret.put(each, value);
		}
		return ret;
	}
}
