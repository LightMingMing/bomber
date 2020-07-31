package com.bomber.functions;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLongFunction extends AbstractFunction {

	private long min;
	private long max;

	@Override
	public String execute() {
		return Long.toString(ThreadLocalRandom.current().nextLong(min, max));
	}

	@Override
	public String getRequiredArgs() {
		return "min, max";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		this.min = Long.parseLong(params.get("min"));
		this.max = Long.parseLong(params.get("max"));
	}
}
