package com.bomber.engine.monitor;

import java.util.List;

import com.bomber.engine.model.BomberContext;
import lombok.Getter;

/**
 * 事件
 *
 * @author MingMing Zhao
 */
@Getter
public class TestingEvent {

	private final String id;

	private final String name;

	private final int requestsPerThread;

	private final List<Integer> threadGroups;

	private final int iterations;

	/**
	 * 当前线程组
	 */
	private final int threadGroupCursor;

	/**
	 * 当前迭代数
	 */
	private final int iteration;

	public TestingEvent(BomberContext ctx) {
		this.id = ctx.getId();
		this.name = ctx.getName();
		this.requestsPerThread = ctx.getRequestsPerThread();
		this.threadGroups = ctx.getThreadGroups();
		this.iterations = ctx.getIterations();
		this.iteration = ctx.getIteration();
		this.threadGroupCursor = ctx.getThreadGroupCursor();
	}
}
