package com.bomber.api.echart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YAxis<T> implements Axis {

	private static final AxisType DEFAULT_TYPE = AxisType.VALUE;

	private String name;

	private int min;

	private int max;

	private double interval;

	private AxisType type;

	private Position position;

	private AxisLabel axisLabel;

	private Series<T> series;

	public YAxis(String name) {
		this(name, Position.LEFT);
	}

	public YAxis(String name, Position position) {
		this(name, position, AxisType.VALUE);
	}

	public YAxis(String name, Position position, AxisType type) {
		this.name = name;
		this.position = position;
		this.type = type;
	}
}
