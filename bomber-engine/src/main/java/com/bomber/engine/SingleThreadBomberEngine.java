package com.bomber.engine;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.lang.NonNull;

import com.bomber.engine.converter.BombardierRequestConverter;
import com.bomber.engine.internal.Counter;
import com.bomber.engine.model.BomberContext;
import com.bomber.engine.model.BomberRequest;
import com.bomber.engine.rpc.BombardierRequest;
import com.bomber.engine.util.ConstantException;
import lombok.extern.slf4j.Slf4j;

/**
 * 单线程 Bomber 引擎
 *
 * @author MingMing Zhao
 */
@Slf4j
public abstract class SingleThreadBomberEngine extends AbstractBomberEngine {

	private static final ConstantException CANCELLED_EXECUTION_EXCEPTION =
			new ConstantException("Task is cancelled");

	protected ExecutorService executor;

	public SingleThreadBomberEngine(BomberContextRegistry registry) {
		super(registry);
		this.executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MINUTES,
				new LinkedBlockingQueue<>(1000),
				r -> {
					Thread thread = new Thread(r);
					thread.setName("bomber-engine");
					return thread;
				},
				(r, e) -> log.warn("task is rejected from bomber engine"));
		// Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
	}

	@Override
	public Future<?> execute(@NonNull BomberRequest request) {
		return executor.submit(newTaskFor(request));
	}

	private Callable<?> newTaskFor(BomberRequest request) {
		BomberContext ctx = new BomberContext(request);
		registry.register(ctx);
		return new Task<>(ctx, () -> {
			try {
				if (fireStarted(ctx)) {
					doExecute(ctx);
				}
			} finally {
				registry.unregister(ctx);
			}
			return null;
		});
	}

	private void doExecute(BomberContext ctx) {
		BombardierRequest request = BombardierRequestConverter.INSTANCE.convert(ctx);
		Counter counter = ctx.rebuildCounter();

		for (; ctx.hasNextThreadGroup(); ctx.nextThreadGroup()) {

			request.setNumberOfConnections(ctx.getNumberOfThreads());
			request.setNumberOfRequests(ctx.getNumberOfRequests());
			request.setStartLine(counter.getAndCount());

			try {
				if (!doIterationExecute(ctx, request)) {
					return;
				}
			} catch (Throwable e) {
				this.fireFailed(ctx, e);
				return;
			}
		}

		this.fireCompleted(ctx);
	}

	private boolean doIterationExecute(BomberContext ctx, BombardierRequest request) throws Throwable {
		for (; ctx.hasNextIteration(); ctx.nextIteration()) {
			if (ctx.isPaused()) {
				this.firePaused(ctx);
				return false;
			}
			this.fireBeforeEachExecute(ctx);
			this.doEachExecute(ctx, request);
		}
		return true;
	}

	protected abstract void doEachExecute(BomberContext ctx, BombardierRequest request) throws Throwable;

	public void destroy() throws Exception {
		// 由于任务执行较久, 这里直接调用 shutdownNow() 方法
		List<Runnable> canceledTasks = executor.shutdownNow();
		for (Runnable canceledTask : canceledTasks) {
			if (canceledTask instanceof FutureTask) {
				Callable<?> task = getCallable((FutureTask<?>) canceledTask);
				if (task instanceof Task) {
					this.fireFailed(((Task<?>) task).ctx, CANCELLED_EXECUTION_EXCEPTION);
				}
			}
		}
	}

	private Callable<?> getCallable(FutureTask<?> futureTask) throws NoSuchFieldException, IllegalAccessException {
		Field field = FutureTask.class.getDeclaredField("callable");
		field.setAccessible(true);
		return (Callable<?>) field.get(futureTask);
	}

	static class Task<V> implements Callable<V> {

		protected final BomberContext ctx;
		protected final Callable<V> callable;

		public Task(BomberContext ctx, Callable<V> callable) {
			this.ctx = ctx;
			this.callable = callable;
		}

		@Override
		public V call() throws Exception {
			return callable.call();
		}
	}
}
