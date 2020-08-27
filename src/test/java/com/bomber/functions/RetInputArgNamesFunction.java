package com.bomber.functions;

import java.util.HashMap;
import java.util.Map;

public class RetInputArgNamesFunction extends AbstractMapFunction {

	private Map<String, String> ret;

	@Override
	public String getRequiredArgs() {
		return "a, b, c, d";
	}

	@Override
	public String getOutputArgNames() {
		return "a, b";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		ret = new HashMap<>();
		ret.put("a", params.get("a"));
		ret.put("b", params.get("b"));
	}

	@Override
	public Map<String, String> execute() {
		return ret;
	}
}
