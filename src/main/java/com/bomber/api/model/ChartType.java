package com.bomber.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChartType {

	COLUMN("column"), LINE("line");

	@JsonValue
	private final String name;

	ChartType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
