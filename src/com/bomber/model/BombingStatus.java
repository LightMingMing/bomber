package com.bomber.model;

import org.ironrhino.core.model.Displayable;

public enum BombingStatus implements Displayable {
	NEW, RUNNING, PAUSE, ERROR, COMPLETED;

	@Override
	public String toString() {
		return getDisplayName();
	}
}
