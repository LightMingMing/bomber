package com.bomber.api.echart;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Series<T> {

	private static final SeriesType DEFAULT_TYPE = SeriesType.LINE;

	@Getter
	private final List<T> data;

	@Getter
	@Setter
	private boolean smooth;

	@Getter
	@Setter
	private SeriesType type;

	public Series() {
		this(DEFAULT_TYPE);
	}

	public Series(SeriesType type) {
		this(DEFAULT_TYPE, true);
	}

	public Series(SeriesType type, boolean smooth) {
		this.type = type;
		this.smooth = (type == DEFAULT_TYPE) && smooth;
		this.data = new ArrayList<>();
	}

	public void add(T t) {
		this.data.add(t);
	}

	@SafeVarargs
	public final void add(T... t) {
		this.data.addAll(Arrays.asList(t));
	}

}
