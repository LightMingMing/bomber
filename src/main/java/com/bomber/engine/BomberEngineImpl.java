package com.bomber.engine;

import static com.bomber.model.BombingStatus.COMPLETED;
import static com.bomber.model.BombingStatus.FAILURE;
import static com.bomber.model.BombingStatus.PAUSE;
import static com.bomber.model.BombingStatus.READY;
import static com.bomber.model.BombingStatus.RUNNING;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.ironrhino.rest.RestStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.bomber.converter.HttpHeaderListConverter;
import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.HttpSampleManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.ApplicationInstance;
import com.bomber.model.BombingRecord;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.model.SummaryReport;
import com.bomber.service.BombardierRequest;
import com.bomber.service.BombardierResponse;
import com.bomber.service.BombardierService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BomberEngineImpl implements BomberEngine {

	private final HttpSampleManager httpSampleManager;

	private final BombingRecordManager bombingRecordManager;

	private final SummaryReportManager summaryReportManager;

	private final ExecutorService bombingExecutor;

	private final BombardierService bombardierService;

	private final BomberContextRegistry registry;

	@Value("${fileStorage.uri:file:///${app.context}/assets/}")
	protected URI uri;

	public BomberEngineImpl(HttpSampleManager httpSampleManager, BombingRecordManager bombingRecordManager,
			SummaryReportManager summaryReportManager, BombardierService bombardierService,
			BomberContextRegistry registry, ExecutorService bombingExecutor) {
		this.httpSampleManager = httpSampleManager;
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
		record.setStdDev(latency.getStdDev());

		BombardierResponse.Percentiles percentiles = latency.getPercentiles();
		record.setPoint50(percentiles.getPoint50());
		record.setPoint75(percentiles.getPoint75());
		record.setPoint90(percentiles.getPoint90());
		record.setPoint95(percentiles.getPoint95());
		record.setPoint99(percentiles.getPoint99());

		record.setTps(response.getTps());
		return record;
	}

	@Override
	@Transactional
	public void execute(@NonNull BomberContext ctx) {
		HttpSample httpSample = httpSampleManager.get(ctx.getSampleId());
		if (httpSample == null) {
			throw new IllegalArgumentException("httpSample does not exist");
		}

		BombingRecord record = new BombingRecord();
		record.setName(ctx.getName());
		record.setThreadGroup(ctx.getThreadGroup());
		record.setThreadGroupCursor(ctx.getThreadGroupCursor());
		record.setRequestsPerThread(ctx.getRequestsPerThread());
		record.setHttpSample(httpSample);
		record.setStartTime(new Date());
		record.setStatus(READY);

		bombingRecordManager.save(record);
		ctx.setId(record.getId());

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				bombingExecutor.execute(() -> {
					registry.registerBomberContext(ctx);
					try {
						doExecute(ctx);
					} finally {
						registry.unregisterBomberContext(ctx);
					}
				});
			}
		});
	}

	@Override
	@Transactional
	public void continueExecute(String ctxId) {
		BombingRecord record = bombingRecordManager.get(ctxId);
		if (record == null || (record.getStatus() != PAUSE && record.getStatus() != FAILURE)) {
			return;
		}
		record.setStatus(READY);
		bombingRecordManager.save(record);

		BomberContext ctx = new BomberContext(ctxId);
		ctx.setSampleId(record.getHttpSample().getId());
		ctx.setName(record.getName());
		ctx.setThreadGroup(record.getThreadGroup());
		ctx.setThreadGroupCursor(record.getThreadGroupCursor());
		ctx.setRequestsPerThread(record.getRequestsPerThread());
		ctx.setActiveThreads(record.getActiveThreads());

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				bombingExecutor.execute(() -> {
					registry.registerBomberContext(ctx);
					try {
						doExecute(ctx);
					} finally {
						registry.unregisterBomberContext(ctx);
					}
				});
			}
		});
	}

	@Override
	public void pauseExecute(String ctxId) {
		Optional.ofNullable(registry.get(ctxId)).ifPresent(BomberContext::pause);
	}

	private void doExecute(BomberContext ctx) {
		HttpSample httpSample = httpSampleManager.get(ctx.getSampleId());

		BombingRecord record = bombingRecordManager.get(ctx.getId());
		record.setStatus(RUNNING);
		bombingRecordManager.save(record);

		int requestCount = 0;

		List<Integer> threadGroup = ctx.getThreadGroup();

		for (int i = 0; i < ctx.getThreadGroupCursor(); i++) {
			requestCount += threadGroup.get(i) * ctx.getRequestsPerThread();
		}

		for (int i = ctx.getThreadGroupCursor(); i < threadGroup.size(); i++) {
			int numberOfThreads = threadGroup.get(i);
			int numberOfRequests = numberOfThreads * ctx.getRequestsPerThread();

			ctx.setActiveThreads(numberOfThreads);
			ctx.setThreadGroupCursor(i);
			record.setActiveThreads(numberOfThreads);
			record.setThreadGroupCursor(i);
			if (ctx.isPaused()) {
				record.setStatus(PAUSE);
				bombingRecordManager.save(record);
				return;
			}
			bombingRecordManager.save(record);

			try {
				BombardierRequest request = convertToBombardierRequest(httpSample);
				request.setNumberOfConnections(numberOfThreads);
				request.setNumberOfRequests(numberOfRequests);
				request.setStartLine(requestCount);

				Date startTime = new Date();
				BombardierResponse response = bombardierService.execute(request);

				SummaryReport summaryReport = convertToSummaryReport(response);
				summaryReport.setStartTime(startTime);
				summaryReport.setEndTime(new Date());
				summaryReport.setBombingRecord(record);
				summaryReportManager.save(summaryReport);
			} catch (RestStatus status) {
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
				return;
			} catch (Exception e) {
				log.error("bombardier execute failed", e);
				record.setStatus(FAILURE);
				record.setEndTime(new Date());
				record.setRemark(e.getMessage());
				bombingRecordManager.save(record);
				return;
			}
			requestCount += numberOfRequests;
		}

		record.setStatus(COMPLETED);
		record.setEndTime(new Date());
		bombingRecordManager.save(record);
	}

	protected BombardierRequest convertToBombardierRequest(HttpSample sample) {
		ApplicationInstance app = sample.getApplicationInstance();
		if (app == null) {
			throw new IllegalArgumentException("app can't be null");
		}
		String path = sample.getPath();

		BombardierRequest request = new BombardierRequest();

		request.setUrl(app.getProtocol().name() + "://" + app.getHost() + ":" + app.getPort()
				+ (path.startsWith("/") ? path : "/" + path));
		request.setMethod(sample.getMethod());
		request.setBody(sample.getBody());

		List<HttpHeader> headerList = sample.getHeaders();
		if (headerList != null && !headerList.isEmpty()) {
			String[] headers = new String[headerList.size()];
			for (int i = 0; i < headerList.size(); i++) {
				headers[i] = HttpHeaderListConverter.convertToString(headerList.get(i));
			}
			request.setHeaders(headers);
		}
		if (StringUtils.hasLength(sample.getCsvFilePath()) && StringUtils.hasLength(sample.getVariableNames())) {
			request.setCsvFilePath(uri.getPath() + sample.getCsvFilePath());
			request.setVariableNames(sample.getVariableNames());
		}
		return request;
	}

}
