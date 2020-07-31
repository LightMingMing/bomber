package com.bomber.functions;

import java.util.Map;

public abstract class AbstractFunction implements Function {

	public String getRequiredArgs() {
		return null;
	}

	public String getOptionalArgs() {
		return null;

	}

	public void init(Map<String, String> params) {
		String required = getRequiredArgs();
		if (required != null) {
			if (params == null || params.isEmpty()) {
				throw new IllegalArgumentException("missing required args");
			}
			String[] args = required.split(", *");
			for (String arg : args) {
				if (!params.containsKey(arg)) {
					throw new IllegalArgumentException("missing required arg: '" + arg + "'");
				}
			}
		}
		doInit(params);
	}

	protected abstract void doInit(Map<String, String> params);

}
