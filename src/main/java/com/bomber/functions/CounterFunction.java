package com.bomber.functions;

import java.util.Map;

public class CounterFunction extends AbstractFunction {

	private int counter = 0;

	@Override
	protected void doInit(Map<String, String> params) {

	}

	@Override
	public String execute() {
		return (counter++) + "";
	}

	@Override
	public void skip(int steps) {
		counter += steps;
	}
}
