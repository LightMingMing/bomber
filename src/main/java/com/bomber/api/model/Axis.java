package com.bomber.api.model;

import java.util.ArrayList;
import java.util.List;

public class Axis<T> implements IAxis<T> {

	private final String title;

	private final List<T> series;

	private final ChartType type;

	public Axis(String title) {
		this(title, ChartType.LINE);
	}

	public Axis(String title, ChartType type) {
		this.title = title;
		this.series = new ArrayList<>();
		this.type = type;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public List<T> getSeries() {
		return series;
	}

	@Override
	public ChartType getType() {
		return type;
	}
}
