package com.bomber.api.echart;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Position {

	LEFT("left"), RIGHT("right");

	@JsonValue
	private final String name;

	Position(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
