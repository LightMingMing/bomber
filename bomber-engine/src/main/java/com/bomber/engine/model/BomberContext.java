package com.bomber.engine.model;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.Nullable;

import com.bomber.engine.internal.BenchmarkCounter;
import com.bomber.engine.internal.Counter;
import com.bomber.engine.internal.RequestCounter;
import com.bomber.engine.internal.ThreadCounter;
import com.bomber.engine.internal.ThreadGroupCounter;
import lombok.Getter;

/**
 * 上下文
 *
 * @author MingMing Zhao
 */
public class BomberContext {

	@Getter
	private final Long id;

	@Getter
	private final String name;

	@Getter
	private final HttpRequest httpRequest;

	@Getter
	private final int requestsPerThread;

	@Getter
	private final List<Integer> threadGroups;

	@Getter
	private final int iterations;

	@Nullable
	private final Payload payload;

	@Getter
	private final Scope scope;

	@Getter
	private final int start;

	private final CounterFactory counterFactory;

	/**
	 * 当前线程组
	 */
	@Getter
	private int threadGroupCursor;

	/**
	 * 当前迭代数
	 */
	@Getter
	private int iteration;

	private volatile boolean paused;

	public BomberContext(BomberRequest request) {
		this.id = request.getId();
		this.name = request.getName();
		this.httpRequest = request.getHttpRequest();
		this.requestsPerThread = request.getRequestsPerThread();
		this.threadGroups = request.getThreadGroups();
		this.iterations = request.getIterations();

		this.iteration = request.getIteration();
		this.threadGroupCursor = request.getThreadGroupCursor();

		this.payload = request.getPayload();
		this.scope = payload != null ? payload.getScope() : Scope.Benchmark;
		this.start = payload != null ? payload.getStart() : 0;

		this.counterFactory = new CounterFactory();
	}

	public boolean isPaused() {
		return paused;
	}

	public void pause() {
		this.paused = true;
	}

	public Counter rebuildCounter() {
		return counterFactory.newCounter();
	}

	public Optional<Payload> getPayload() {
		return Optional.ofNullable(payload);
	}

	public boolean hasNextThreadGroup() {
		return threadGroupCursor < threadGroups.size();
	}

	public void nextThreadGroup() {
		threadGroupCursor++;
		iteration = 0; // reset iteration
	}

	public boolean hasNextIteration() {
		return iteration < iterations;
	}

	public void nextIteration() {
		iteration++;
	}

	/**
	 * 当前线程数
	 *
	 * @return 当前线程数
	 */
	public int getNumberOfThreads() {
		return threadGroups.get(threadGroupCursor);
	}

	/**
	 * 当前请求数
	 *
	 * @return 当前请求数
	 */
	public int getNumberOfRequests() {
		return getNumberOfThreads() * requestsPerThread;
	}

	class CounterFactory {
		Counter newCounter() {
			switch (scope) {
				case Benchmark:
					return new BenchmarkCounter(start);
				case Group:
					return new ThreadGroupCounter(start, threadGroupCursor);
				case Thread:
					return new ThreadCounter(start, threadGroups, threadGroupCursor);
				default:
					return new RequestCounter(start, threadGroups, threadGroupCursor, requestsPerThread);
			}
		}
	}
}
