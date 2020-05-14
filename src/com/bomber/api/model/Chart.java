package com.bomber.api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Chart<X, Y> {

	@Getter
	private final String title;

	@Getter
	private final String subTitle;
	@JsonProperty("yAxis")
	private final List<YAxis<Y>> yAxis;
	@Setter
	@JsonProperty("xAxis")
	private XAxis<X> xAxis;

	public Chart(String title, String subTitle) {
		this.title = title;
		this.subTitle = subTitle;
		this.yAxis = new ArrayList<>();
	}

	public void addYAxis(YAxis<Y> yAxis) {
		this.yAxis.add(yAxis);
	}

	@JsonIgnore
	public XAxis<X> getXAxis() {
		return xAxis;
	}

	@JsonIgnore
	public List<YAxis<Y>> getYAxis() {
		return yAxis;
	}

	@SafeVarargs
	public final void setAxis(XAxis<X> xAxis, YAxis<Y>... yAxes) {
		setXAxis(xAxis);
		for (YAxis<Y> yAxis : yAxes) {
			addYAxis(yAxis);
		}
	}
}
