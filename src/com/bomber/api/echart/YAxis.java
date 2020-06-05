package com.bomber.api.echart;

import lombok.Getter;
import lombok.Setter;

import static com.bomber.api.echart.AxisLabel.DEFAULT_FORMATTER;

@Getter
@Setter
public class YAxis implements Axis {

	private static final AxisType DEFAULT_TYPE = AxisType.VALUE;

	private String name;

	private double min;

	private double max;

	private double interval;

	private AxisType type;

	private Position position;

	private AxisLabel axisLabel;

	public YAxis(String name) {
		this(name, DEFAULT_FORMATTER, Position.LEFT);
	}

	public YAxis(String name, String formatter) {
		this(name, formatter, Position.LEFT);
	}

	public YAxis(String name, Position position) {
		this(name, DEFAULT_FORMATTER, position);
	}

	public YAxis(String name, String formatter, Position position) {
		this(name, formatter, position, AxisType.VALUE);
	}

	public YAxis(String name, String formatter, Position position, AxisType type) {
		this.name = name;
		this.axisLabel = new AxisLabel(formatter);
		this.position = position;
		this.type = type;
	}
}
