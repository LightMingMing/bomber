package com.bomber.functions;

import java.util.Map;

public class SumFunction extends AbstractFunction {

	private String a;
	private String b;

	@Override
	public String getRequiredArgs() {
		return "a, b";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		this.a = params.get("a");
		this.b = params.get("b");
	}

	@Override
	public String execute() {
		return (Long.parseLong(a) + Long.parseLong(b)) + "";
	}
}
