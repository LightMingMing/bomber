package com.bomber.functions;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomIntegerFunction extends AbstractStringFunction {

	private int min;
	private int max;

	@Override
	public String execute() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(min, max));
	}

	@Override
	public String getRequiredArgs() {
		return "min, max";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		this.min = Integer.parseInt(params.get("min"));
		this.max = Integer.parseInt(params.get("max"));
	}
}
