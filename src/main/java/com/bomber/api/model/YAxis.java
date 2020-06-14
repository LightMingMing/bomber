package com.bomber.api.model;

public class YAxis<T> extends Axis<IAxis<T>> {

	private final boolean opposite;

	public YAxis(String title) {
		this(title, false);
	}

	public YAxis(String title, boolean opposite) {
		super(title, null);
		this.opposite = opposite;
	}

	public boolean isOpposite() {
		return opposite;
	}

	@SafeVarargs
	public final void add(IAxis<T>... axes) {
		for (IAxis<T> axis : axes)
			add(axis);
	}
}
