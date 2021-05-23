package com.bomber.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import com.bomber.engine.model.BomberContext;
import com.bomber.engine.model.BomberRequest;
import com.bomber.engine.model.HttpRequest;
import com.bomber.engine.model.Payload;
import com.bomber.engine.model.Result;
import com.bomber.engine.model.Scope;
import com.bomber.engine.monitor.TestingListener;
import com.bomber.engine.rpc.BombardierRequest;

/**
 * @author MingMing Zhao
 */
class SingleThreadBomberEngineTest {

	public static HttpRequest httpRequest() {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl("http://localhost:8080/api/hello");
		httpRequest.setMethod(HttpMethod.GET);
		return httpRequest;
	}

	public static Payload payload(Scope scope, int start) {
		Payload payload = new Payload();
		payload.setUrl("http://localhost:8080/api/payload");
		payload.setScope(scope);
		payload.setStart(start);
		payload.setFunctionInfos(Collections.emptyList());
		return payload;
	}

	@Test
	public void testRequestScope() {
		BomberRequest request = new BomberRequest();
		request.setId("1");
		request.setName("test");
		request.setHttpRequest(httpRequest());

		request.setIterations(1);
		request.setThreadGroups(Arrays.asList(1, 2, 5, 10));
		request.setRequestsPerThread(10);
		request.setPayload(payload(Scope.Request, 0));

		testTemplate(request, req -> {
			assertThat(req.getNumberOfConnections()).isEqualTo(10);
			assertThat(req.getNumberOfRequests()).isEqualTo(100);
			assertThat(req.getStartLine()).isEqualTo(80);
		});
	}

	@Test
	public void testThreadScope() {
		BomberRequest request = new BomberRequest();
		request.setId("1");
		request.setName("test");
		request.setHttpRequest(httpRequest());

		request.setIterations(1);
		request.setThreadGroups(Arrays.asList(1, 2, 5, 10));
		request.setRequestsPerThread(10);
		request.setPayload(payload(Scope.Thread, 0));

		testTemplate(request, req -> {
			assertThat(req.getNumberOfConnections()).isEqualTo(10);
			assertThat(req.getNumberOfRequests()).isEqualTo(100);
			assertThat(req.getStartLine()).isEqualTo(8);
		});
	}

	@Test
	public void testGroupScope() {
		BomberRequest request = new BomberRequest();
		request.setId("1");
		request.setName("test");
		request.setHttpRequest(httpRequest());

		request.setIterations(1);
		request.setThreadGroups(Arrays.asList(1, 2, 5, 10));
		request.setRequestsPerThread(10);
		request.setPayload(payload(Scope.Group, 0));

		testTemplate(request, req -> {
			assertThat(req.getNumberOfConnections()).isEqualTo(10);
			assertThat(req.getNumberOfRequests()).isEqualTo(100);
			assertThat(req.getStartLine()).isEqualTo(3);
		});
	}

	@Test
	public void testBenchmarkScope() {
		BomberRequest request = new BomberRequest();
		request.setId("1");
		request.setName("test");
		request.setHttpRequest(httpRequest());

		request.setIterations(1);
		request.setThreadGroups(Arrays.asList(1, 2, 5, 10));
		request.setRequestsPerThread(10);
		request.setPayload(payload(Scope.Benchmark, 0));

		testTemplate(request, req -> {
			assertThat(req.getNumberOfConnections()).isEqualTo(10);
			assertThat(req.getNumberOfRequests()).isEqualTo(100);
			assertThat(req.getStartLine()).isEqualTo(0);
		});
	}

	@Test
	public void testPause() {
		// given
		TestingListener listener = Mockito.mock(TestingListener.class);
		given(listener.started(any())).willReturn(true);

		SimpleBomberEngine engine = new AutoPauseBomberEngine();
		engine.register(listener);

		// when
		BomberRequest request = new BomberRequest();
		request.setId("1");
		request.setName("test");
		request.setHttpRequest(httpRequest());

		request.setIterations(1);
		request.setThreadGroups(Arrays.asList(1, 2, 5, 10));
		request.setRequestsPerThread(10);

		Future<?> result = engine.execute(request);
		engine.pause("1");

		// then
		assertThat(result).succeedsWithin(1, TimeUnit.SECONDS);
		then(listener).should().started(any());
		then(listener).should().paused(any());
		then(listener).should(never()).completed(any());
	}

	private void testTemplate(BomberRequest request, Consumer<BombardierRequest> lastVerify) {
		// given
		TestingListener listener = Mockito.mock(TestingListener.class);
		given(listener.started(any())).willReturn(true);

		ArgumentCaptor<BombardierRequest> requestCaptor = ArgumentCaptor.forClass(BombardierRequest.class);
		SimpleBomberEngine engine = spy(new SimpleBomberEngine());
		willCallRealMethod().given(engine).doEachExecute(any(), requestCaptor.capture());
		engine.register(listener);

		// when
		Future<?> result = engine.execute(request);
		assertThat(result).succeedsWithin(1, TimeUnit.SECONDS);

		// then
		int executeTimes = request.getThreadGroups().size() * request.getIterations();

		then(listener).should().started(any());
		then(listener).should(times(executeTimes)).afterEachExecute(any(), any());
		then(listener).should().completed(any());

		// 实现上多次执行返回的是同一个 BombardierRequest, 因此这里校验的是终态
		// 保险起见, 取最后一个
		lastVerify.accept(requestCaptor.getAllValues().get(executeTimes - 1));
	}

	private static class SimpleBomberEngine extends SingleThreadBomberEngine {

		public SimpleBomberEngine() {
			super(new BomberContextRegistryImpl());
		}

		@Override
		protected void doEachExecute(BomberContext ctx, BombardierRequest request) {
			this.fireEachExecute(ctx, mock(Result.class));
		}
	}

	private static class AutoPauseBomberEngine extends SimpleBomberEngine {

		@Override
		protected void doEachExecute(BomberContext ctx, BombardierRequest request) {
			super.doEachExecute(ctx, request);
			// automatic pause
			ctx.pause();
		}
	}
}