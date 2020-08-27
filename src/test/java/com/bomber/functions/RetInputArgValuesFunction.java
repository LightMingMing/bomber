package com.bomber.functions;

import java.util.HashMap;
import java.util.Map;

public class RetInputArgValuesFunction extends AbstractMapFunction {

	private Map<String, String> ret;

	private String[] args;

	private String value;

	@Override
	public String getRequiredArgs() {
		return "value, retArgs";
	}

	@Override
	public String getOutputArgValues() {
		return "retArgs";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		ret = new HashMap<>();
		args = params.get("retArgs").split(", *");
		value = params.get("value");
	}

	@Override
	public Map<String, String> execute() {
		for (String arg : args) {
			ret.put(arg, value);
		}
		return ret;
	}
}