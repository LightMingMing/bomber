package com.bomber.functions;

import java.util.Map;

public class PropertyFunction extends AbstractStringFunction {

	private String value;

	public static void main(String[] args) {
		new PropertyFunction();
	}

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
