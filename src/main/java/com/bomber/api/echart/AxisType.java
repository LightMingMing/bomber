package com.bomber.api.echart;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AxisType {
	VALUE("value"), CATEGORY("category");

	@JsonValue
	private final String name;

	AxisType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
