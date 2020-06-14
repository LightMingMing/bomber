package com.bomber.api.echart;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SeriesType {

	/**
	 * 柱状图
	 */
	BAR("bar"),
	/**
	 * 折线图
	 */
	LINE("line"),
	/**
	 * 饼图
	 */
	PIE("pie"),
	/**
	 * 散点图
	 */
	SCATTER("scatter"),
	/**
	 * 关系图
	 */
	GRAPH("graph"),
	/**
	 * 树图
	 */
	TREE("tree");

	@JsonValue
	private final String name;

	SeriesType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
