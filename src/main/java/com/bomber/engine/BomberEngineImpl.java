package com.bomber.engine;

import static com.bomber.model.BombingStatus.COMPLETED;
import static com.bomber.model.BombingStatus.FAILURE;
import static com.bomber.model.BombingStatus.PAUSE;
import static com.bomber.model.BombingStatus.RUNNING;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.ironrhino.rest.RestStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.SummaryReport;
import com.bomber.rpc.BombardierRequest;
import com.bomber.rpc.BombardierResponse;
import com.bomber.rpc.BombardierService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BomberEngineImpl implements BomberEngine {

	private final BombingRecordManager bombingRecordManager;

	private final SummaryReportManager summaryReportManager;

	private final ExecutorService bombingExecutor;

	private final BombardierService bombardierService;

	private final BomberContextRegistry registry;

	public BomberEngineImpl(BombingRecordManager bombingRecordManager, SummaryReportManager summaryReportManager,
			BombardierService bombardierService, BomberContextRegistry registry, ExecutorService bombingExecutor) {
		this.bombingRecordManager = bombingRecordManager;
		this.summaryReportManager = summaryReportManager;
		this.bombardierService = bombardierService;
		this.registry = registry;
		this.bombingExecutor = bombingExecutor;
	}

	protected static SummaryReport convertToSummaryReport(BombardierResponse response) {
		SummaryReport record = new SummaryReport();
		record.setNumberOfThreads(response.getNumConns());
		record.setNumberOfRequests(response.getNumReqs());

		BombardierResponse.StatusStats status = response.getStatus();
		record.setReq1xx(status.getReq1xx());
		record.setReq2xx(status.getReq2xx());
		record.setReq3xx(status.getReq3xx());
		record.setReq4xx(status.getReq4xx());
		record.setReq5xx(status.getReq5xx());
		record.setOther(status.getOther());

		BombardierResponse.LatencyStats latency = response.getLatency();
		record.setAvg(latency.getAvg());
		record.setMax(latency.getMax());
		record.setMin(latency.getMin());
		record.setStdDev(latency.getStdDev());

		BombardierResponse.Percentiles percentiles = latency.getPercentiles();
		record.setPoint25(percentiles.getPoint25());
		record.setPoint50(percentiles.getPoint50());
		record.setPoint75(percentiles.getPoint75());
		record.setPoint90(percentiles.getPoint90());
		record.setPoint95(percentiles.getPoint95());
		record.setPoint99(percentiles.getPoint99());

		record.setTps(response.getTps());
		record.setErrorCount(response.getErrorCount());
		return record;
	}

	protected static BombardierRequest createBombardierRequest(HttpSampleSnapshot snapshot, Scope scope) {
		BombardierRequest request = new BombardierRequest();
		request.setMethod(snapshot.getMethod());
		request.setUrl(snapshot.getUrl());
		request.setHeaders(snapshot.getHeaders());
		request.setBody(snapshot.getBody());
		request.setPayloadFile(snapshot.getPayloadFile());
		request.setVariableNames(snapshot.getVariableNames());
		request.setPayloadUrl(snapshot.getPayloadUrl());
		snapshot.getAssertions().forEach(each -> request.addAssertion(each.getAsserter(), each.getExpression(),
				each.getCondition(), each.getExpected()));
		if (scope == Scope.Request) {
			request.setScope("request");
		} else if (scope == Scope.Thread) {
			request.setScope("thread");
		} else {
			request.setScope("benchmark");
		}
		return request;
	}

	public static Counter createCounter(BomberContext ctx) {
		Scope scope = ctx.getScope();
		if (scope == Scope.Request) {
			return new RequestCounter(ctx.getStart(), ctx.getThreadGroups(), ctx.getThreadGroupCursor(),
					ctx.getRequestsPerThread());
		} else if (scope == Scope.Thread) {
			return new ThreadCounter(ctx.getStart(), ctx.getThreadGroups(), ctx.getThreadGroupCursor());
		} else if (scope == Scope.Group) {
			return new ThreadGroupCounter(ctx.getStart(), ctx.getThreadGroupCursor());
		} else {
			return new BenchmarkCounter(ctx.getStart());
		}
	}

	@Override
	public void execute(@NonNull BomberContext ctx) {
		bombingExecutor.execute(() -> {
			registry.registerBomberContext(ctx);
			try {
				doExecute(ctx);
			} finally {
				registry.unregisterBomberContext(ctx);
			}
		});
	}

	@Override
	public void pauseExecute(String id) {
		Optional.ofNullable(registry.get(id)).ifPresent(BomberContext::pause);
	}

	private void doExecute(BomberContext ctx) {
		HttpSampleSnapshot httpSampleSnapshot = ctx.getHttpSampleSnapshot();

		BombingRecord record = bombingRecordManager.get(ctx.getId());
		if (record == null) {
			return; // is removed
		}
		if (record.getStartTime() == null) {
			record.setStartTime(new Date()); // start time
		}
		record.setStatus(RUNNING);
		bombingRecordManager.save(record);

		BombardierRequest request = createBombardierRequest(httpSampleSnapshot, ctx.getScope());

		for (int i = ctx.getCurrentIterations(); i < ctx.getIterations();) {

			Counter counter = createCounter(ctx);

			for (int j = ctx.getThreadGroupCursor(); j < ctx.getThreadGroups().size(); j++) {
				int numberOfThreads = ctx.getThreadGroups().get(j);
				int numberOfRequests = numberOfThreads * ctx.getRequestsPerThread();

				ctx.setActiveThreads(numberOfThreads);
				ctx.setThreadGroupCursor(j);
				record.setActiveThreads(numberOfThreads);
				record.setThreadGroupCursor(j);
				record.setCurrentIterations(i);
				if (ctx.isPaused()) {
					record.setStatus(PAUSE);
					bombingRecordManager.save(record);
					return;
				}
				bombingRecordManager.save(record);

				request.setNumberOfConnections(numberOfThreads);
				request.setNumberOfRequests(numberOfRequests);
				request.setStartLine(counter.getAndCount());

				try {
					Date startTime = new Date();
					BombardierResponse response = bombardierService.execute(request);
					saveSummaryReport(response, record, startTime);
				} catch (RestStatus status) {
					handleException(record, status);
					return;
				} catch (Exception e) {
					handleException(record, e);
					return;
				}
			}

			if (++i < ctx.getIterations()) {
				ctx.setThreadGroupCursor(0);
			}
		}

		record.setStatus(COMPLETED);
		record.setEndTime(new Date());
		record.setCurrentIterations(record.getCurrentIterations());
		bombingRecordManager.save(record);
	}

	private void handleException(BombingRecord record, RestStatus status) {
		log.error("bombardier execute failed", status);
		String message = status.getStatus();
		if (status.getMessage() != null) {
			message = status.getMessage();
		} else if (status.getCause() != null && status.getCause().getMessage() != null) {
			message = status.getCause().getMessage();
		}
		record.setStatus(FAILURE);
		record.setRemark(message);
		record.setEndTime(new Date());
		bombingRecordManager.save(record);
	}

	private void handleException(BombingRecord record, Exception e) {
		log.error("bombardier execute failed", e);
		record.setStatus(FAILURE);
		record.setEndTime(new Date());
		record.setRemark(e.getMessage());
		bombingRecordManager.save(record);
	}

	private void saveSummaryReport(BombardierResponse response, BombingRecord record, Date startTime) {
		SummaryReport summaryReport = convertToSummaryReport(response);
		summaryReport.setStartTime(startTime);
		summaryReport.setEndTime(new Date());
		summaryReport.setBombingRecord(record);
		summaryReportManager.save(summaryReport);
	}
}
