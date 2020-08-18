package com.bomber.functions;

import java.util.Map;

public class PropertyFunction extends AbstractFunction {

	private String value;

	@Override
	public String getRequiredArgs() {
		return "value";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		this.value = params.get("value");
	}

	@Override
	public String execute() {
		return value;
	}
}
