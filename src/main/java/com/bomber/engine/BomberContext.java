package com.bomber.engine;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class BomberContext {

	@Getter
	@Setter
	private String id; // recordId

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private HttpSampleSnapshot httpSampleSnapshot;

	@Getter
	@Setter
	private List<Integer> threadGroups;

	@Getter
	@Setter
	private int threadGroupCursor;

	@Getter
	@Setter
	private int requestsPerThread;

	@Getter
	@Setter
	private int activeThreads;

	@Getter
	@Setter
	private Scope scope = Scope.Request;

	@Getter
	@Setter
	private int start;

	@Setter
	private volatile boolean paused;

	public BomberContext() {

	}

	public BomberContext(String id) {
		this.id = id;
	}

	public boolean isPaused() {
		return paused;
	}

	public void pause() {
		this.paused = true;
	}

}
