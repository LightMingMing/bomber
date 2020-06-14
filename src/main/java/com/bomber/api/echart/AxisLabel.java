package com.bomber.api.echart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AxisLabel {

	protected static final String DEFAULT_FORMATTER = "{value}";

	private String formatter; // such as '{value} ml', '{value} °C'

	public AxisLabel() {
		this(DEFAULT_FORMATTER);
	}

	public AxisLabel(String formatter) {
		this.formatter = formatter;
	}
}
