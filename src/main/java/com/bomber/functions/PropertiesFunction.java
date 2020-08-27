package com.bomber.functions;

import java.util.Collections;
import java.util.Map;

public class PropertiesFunction extends AbstractMapFunction {

	private Map<String, String> properties;

	@Override
	protected void doInit(Map<String, String> params) {
		this.properties = Collections.unmodifiableMap(params);
	}

	@Override
	public boolean outputAllInputArgs() {
		return true;
	}

	@Override
	public Map<String, String> execute() {
		return properties;
	}
}
