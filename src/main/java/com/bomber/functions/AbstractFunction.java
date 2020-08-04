package com.bomber.functions;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFunction implements Function {

	public String getRequiredArgs() {
		return null;
	}

	public String getOptionalArgs() {
		return null;

	}

	public void init(String params) {
		if (params == null) {
			init((Map<String, String>) null);
		} else {
			Map<String, String> map = new HashMap<>();
			for (String argumentValue : params.split(", *")) {
				String[] arr = argumentValue.split("=");
				if (arr.length != 2) {
					throw new IllegalArgumentException("Invalid argumentValue format '" + argumentValue + "'");
				}
				map.put(arr[0], arr[1]);
			}
			init(map);
		}

	}

	public void init(Map<String, String> params) {
		String required = getRequiredArgs();
		if (required != null) {
			if (params == null || params.isEmpty()) {
				throw new IllegalArgumentException("Missing required args");
			}
			String[] args = required.split(", *");
			for (String arg : args) {
				if (!params.containsKey(arg)) {
					throw new IllegalArgumentException("Missing required arg: '" + arg + "'");
				}
			}
		}
		doInit(params);
	}

	protected abstract void doInit(Map<String, String> params);

}
