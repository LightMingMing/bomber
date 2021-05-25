package com.bomber.engine.monitor;

import com.bomber.engine.model.Result;

/**
 * 监听器
 *
 * @author MingMing Zhao
 */
public interface TestingListener {

	default boolean started(TestingEvent event) {
		return true;
	}

	default void paused(TestingEvent event) {
	}

	default void completed(TestingEvent event) {
	}

	default void metric(TestingEvent event, int doneRequests) {
	}

	default void failed(TestingEvent event, Throwable e) {
	}

	default void beforeEachExecute(TestingEvent event) {
	}

	default void afterEachExecute(TestingEvent event, Result result) {
	}
}
