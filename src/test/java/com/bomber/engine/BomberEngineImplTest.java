package com.bomber.engine;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.Test;

public class BomberEngineImplTest {

	@Test
	public void testBenchmarkCounter() {
		BomberContext ctx = new BomberContext();
		ctx.setScope(Scope.Benchmark);
		ctx.setStart(100);

		Counter counter = BomberEngineImpl.createCounter(ctx);

		assertThat(counter).isInstanceOf(BenchmarkCounter.class);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(100);
	}

	@Test
	public void testThreadGroupCounter() {
		BomberContext ctx = new BomberContext();
		ctx.setScope(Scope.Group);
		ctx.setStart(100);
		ctx.setThreadGroupCursor(0);

		Counter counter = BomberEngineImpl.createCounter(ctx);

		assertThat(counter).isInstanceOf(BenchmarkCounter.class);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(101);
		assertThat(counter.getAndCount()).isEqualTo(102);
	}

	@Test
	public void testThreadCounter() {
		BomberContext ctx = new BomberContext();
		ctx.setScope(Scope.Thread);
		ctx.setStart(100);
		ctx.setThreadGroups(Arrays.asList(1, 5, 10, 20));
		ctx.setThreadGroupCursor(0);

		Counter counter = BomberEngineImpl.createCounter(ctx);

		assertThat(counter).isInstanceOf(BenchmarkCounter.class);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(101);
		assertThat(counter.getAndCount()).isEqualTo(106);
		assertThat(counter.getAndCount()).isEqualTo(116);
	}

	@Test
	public void testRequestCounter() {
		BomberContext ctx = new BomberContext();
		ctx.setScope(Scope.Request);
		ctx.setStart(100);
		ctx.setThreadGroups(Arrays.asList(1, 5, 10, 20));
		ctx.setThreadGroupCursor(0);
		ctx.setRequestsPerThread(10);

		Counter counter = BomberEngineImpl.createCounter(ctx);

		assertThat(counter).isInstanceOf(BenchmarkCounter.class);
		assertThat(counter.getAndCount()).isEqualTo(100);
		assertThat(counter.getAndCount()).isEqualTo(110);
		assertThat(counter.getAndCount()).isEqualTo(160);
		assertThat(counter.getAndCount()).isEqualTo(260);
	}

}