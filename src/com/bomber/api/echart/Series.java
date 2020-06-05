package com.bomber.api.echart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

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

	@JsonProperty("yAxisIndex")
	private int yAxisIndex;

	public Series() {
		this(0, DEFAULT_TYPE);
	}

	public Series(int yAxisIndex) {
		this(yAxisIndex, DEFAULT_TYPE);
	}

	public Series(int yAxisIndex, SeriesType type) {
		this(yAxisIndex, type, false);
	}

	public Series(int yAxisIndex, SeriesType type, boolean smooth) {
		this.yAxisIndex = yAxisIndex;
		this.type = type;
		this.smooth = (type == DEFAULT_TYPE) && smooth;
		this.data = new ArrayList<>();
	}

	@JsonIgnore
	public int getYAxisIndex() {
		return yAxisIndex;
	}

	public void setYAxisIndex(int yAxisIndex) {
		this.yAxisIndex = yAxisIndex;
	}

	void add(T t) {
		this.data.add(t);
	}

	@SafeVarargs
	public final void add(T... t) {
		this.data.addAll(Arrays.asList(t));
	}

}
