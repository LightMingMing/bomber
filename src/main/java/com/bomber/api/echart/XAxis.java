package com.bomber.api.echart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class XAxis<T> implements Axis {

	private static final AxisType DEFAULT_TYPE = AxisType.CATEGORY;

	@Getter
	private final List<T> data;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private AxisType type;

	public XAxis(String name) {
		this(name, DEFAULT_TYPE);
	}

	public XAxis(String name, AxisType type) {
		this.name = name;
		this.type = type;
		this.data = new ArrayList<>();
	}

	public void add(T t) {
		this.data.add(t);
	}

	@SafeVarargs
	public final void add(T... t) {
		data.addAll(Arrays.asList(t));
	}
}
