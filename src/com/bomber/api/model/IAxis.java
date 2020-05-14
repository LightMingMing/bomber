package com.bomber.api.model;

import java.util.List;

public interface IAxis<T> {

	String getTitle();

	List<T> getSeries();

	ChartType getType();

	default void add(T t) {
		getSeries().add(t);
	}
}
