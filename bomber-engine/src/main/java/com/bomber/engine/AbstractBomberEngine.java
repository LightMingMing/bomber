package com.bomber.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bomber.engine.model.BomberContext;
import com.bomber.engine.model.Result;
import com.bomber.engine.monitor.TestingEvent;
import com.bomber.engine.monitor.TestingListener;
import com.bomber.engine.monitor.TestingNotifier;

/**
 * 抽象引擎实现
 *
 * @author MingMing Zhao
 */
public abstract class AbstractBomberEngine implements BomberEngine, TestingNotifier {

	private final List<TestingListener> listeners = new CopyOnWriteArrayList<>();

	protected BomberContextRegistry registry;

	public AbstractBomberEngine(BomberContextRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void pause(String id) {
		Optional.ofNullable(registry.get(id)).ifPresent(BomberContext::pause);
	}

	@Override
	public void register(TestingListener... listeners) {
		this.listeners.addAll(Arrays.asList(listeners));
	}

	@Override
	public boolean fireStarted(BomberContext ctx) {
		TestingEvent event = new TestingEvent(ctx);
		for (TestingListener listener : listeners) {
			if (!listener.started(event)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void firePaused(BomberContext ctx) {
		TestingEvent event = new TestingEvent(ctx);
		for (TestingListener listener : listeners) {
			listener.paused(event);
		}
	}

	@Override
	public void fireFailed(BomberContext ctx, Throwable e) {
		TestingEvent event = new TestingEvent(ctx);
		for (TestingListener listener : listeners) {
			listener.failed(event, e);
		}
	}

	@Override
	public void fireMetric(BomberContext ctx, int doneRequests) {
		TestingEvent event = new TestingEvent(ctx);
		for (TestingListener listener : listeners) {
			listener.metric(event, doneRequests);
		}
	}

	@Override
	public void fireCompleted(BomberContext ctx) {
		TestingEvent event = new TestingEvent(ctx);
		for (TestingListener listener : listeners) {
			listener.completed(event);
		}
	}

	@Override
	public void fireEachExecute(BomberContext ctx, Result result) {
		TestingEvent event = new TestingEvent(ctx);
		for (TestingListener listener : listeners) {
			listener.afterEachExecute(event, result);
		}
	}
}
