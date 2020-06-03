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
	private final List<YAxis<Y>> yAxes;

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
	}

	@JsonIgnore
	public XAxis<X> getXAxis() {
		return xAxis;
	}

	public void setXAxis(XAxis<X> xAxis) {
		this.xAxis = xAxis;
	}

	@JsonIgnore
	public List<YAxis<Y>> getYAxes() {
		return yAxes;
	}

	public void addYAxis(YAxis<Y> yAxis) {
		this.yAxes.add(yAxis);
	}

	@SafeVarargs
	public final void addYAxis(YAxis<Y>... yAxes) {
		this.yAxes.addAll(Arrays.asList(yAxes));
	}
}
