package com.bomber.model;

import org.ironrhino.core.model.Displayable;

public enum BombingStatus implements Displayable {
	READY, RUNNING, PAUSE, FAILURE, COMPLETED;

	@Override
	public String toString() {
		return getDisplayName();
	}
}
