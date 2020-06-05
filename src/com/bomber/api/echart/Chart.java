package com.bomber.api.echart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Chart<X, Y> {

	@JsonProperty("yAxis")
	private final List<YAxis> yAxes;

	@Getter
	@JsonProperty("series")
	private final List<Series<Y>> series;

	@Getter
	@Setter
	private Title title;

	@Getter
	@Setter
	private List<String> colors;

	@Getter
	@Setter
	private Legend legend;

	@JsonProperty("xAxis")
	private XAxis<X> xAxis;

	public Chart() {
		this.yAxes = new ArrayList<>();
		this.series = new ArrayList<>();
	}

	@JsonIgnore
	public XAxis<X> getXAxis() {
		return xAxis;
	}

	public void setXAxis(XAxis<X> xAxis) {
		this.xAxis = xAxis;
	}

	@JsonIgnore
	public List<YAxis> getYAxes() {
		return yAxes;
	}

	public void addYAxis(YAxis yAxis) {
		this.yAxes.add(yAxis);
	}

	public void addYAxis(YAxis... yAxes) {
		this.yAxes.addAll(Arrays.asList(yAxes));
	}

	public void addSeries(Series<Y> series) {
		this.series.add(series);
	}
	@SafeVarargs
	public final void addSeries(Series<Y>... series) {
		this.series.addAll(Arrays.asList(series));
	}
}
