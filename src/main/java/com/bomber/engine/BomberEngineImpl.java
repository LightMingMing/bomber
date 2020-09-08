package com.bomber.engine;

import static com.bomber.api.controller.PayloadController.getPayloadApiUrl;
import static com.bomber.model.BombingStatus.COMPLETED;
import static com.bomber.model.BombingStatus.FAILURE;
import static com.bomber.model.BombingStatus.PAUSE;
import static com.bomber.model.BombingStatus.READY;
import static com.bomber.model.BombingStatus.RUNNING;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.bomber.model.Payload;
import com.bomber.model.SummaryReport;
import com.bomber.service.BombardierRequest;
import com.bomber.service.BombardierResponse;
import com.bomber.service.BombardierService;
import com.bomber.util.ValueReplacer;

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

	@Value("${fileStorage.uri}")
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

	protected static HttpSampleSnapshot buildHttpSampleSnapshot(HttpSample sample) {
		ApplicationInstance app = sample.getApplicationInstance();

		String path = sample.getPath();

		HttpSampleSnapshot snapshot = new HttpSampleSnapshot();
		snapshot.setMethod(sample.getMethod());
		snapshot.setUrl(app.getUrl() + (path.startsWith("/") ? path : "/" + path));
		snapshot.setBody(sample.getBody());

		List<HttpHeader> headerList = sample.getHeaders();
		if (headerList != null && !headerList.isEmpty()) {
			String[] headers = new String[headerList.size()];
			for (int i = 0; i < headerList.size(); i++) {
				headers[i] = HttpHeaderListConverter.convertToString(headerList.get(i));
			}
			snapshot.setHeaders(headers);
		}

		snapshot.setVariableNames(sample.getVariableNames());
		snapshot.setVariableFilePath(sample.getCsvFilePath());

		Payload payload = sample.getPayload();
		if (payload != null) {
			snapshot.setPayloadId(payload.getId());
		}
		return snapshot;
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

		HttpSampleSnapshot snapshot = buildHttpSampleSnapshot(httpSample);
		ctx.setHttpSampleSnapshot(snapshot);

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

		HttpSample httpSample = record.getHttpSample();
		HttpSampleSnapshot snapshot = buildHttpSampleSnapshot(httpSample);

		BomberContext ctx = new BomberContext(ctxId);
		ctx.setSampleId(httpSample.getId());
		ctx.setHttpSampleSnapshot(snapshot);
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
		HttpSampleSnapshot httpSampleSnapshot = ctx.getHttpSampleSnapshot();

		BombingRecord record = bombingRecordManager.get(ctx.getId());
		record.setStatus(RUNNING);
		bombingRecordManager.save(record);

		int requestCount = 0;
		int threadCount = 0;

		List<Integer> threadGroup = ctx.getThreadGroup();

		for (int i = 0; i < ctx.getThreadGroupCursor(); i++) {
			requestCount += threadGroup.get(i) * ctx.getRequestsPerThread();
			threadCount += threadGroup.get(i);
		}

		BombardierRequest request = convertToBombardierRequest(httpSampleSnapshot);
		if (ctx.getScope() == Scope.Request) {
			request.setScope("request");
		} else if (ctx.getScope() == Scope.Thread) {
			request.setScope("thread");
		} else {
			request.setScope("benchmark");
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
				request.setNumberOfConnections(numberOfThreads);
				request.setNumberOfRequests(numberOfRequests);
				if (ctx.getScope() == Scope.Request) {
					request.setStartLine(requestCount);
				} else if (ctx.getScope() == Scope.Thread) {
					request.setStartLine(threadCount);
				} else if (ctx.getScope() == Scope.Group) {
					request.setStartLine(i);
				} else {
					// TODO support custom start line ?
					request.setStartLine(0);
				}

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
			threadCount += numberOfThreads;
		}

		record.setStatus(COMPLETED);
		record.setEndTime(new Date());
		bombingRecordManager.save(record);
	}

	protected BombardierRequest convertToBombardierRequest(HttpSampleSnapshot snapshot) {
		BombardierRequest request = new BombardierRequest();
		request.setMethod(snapshot.getMethod());
		request.setUrl(snapshot.getUrl());
		request.setHeaders(snapshot.getHeaders());
		request.setBody(snapshot.getBody());

		String payloadFile = snapshot.getVariableFilePath();
		String variableNames = snapshot.getVariableNames();

		if (StringUtils.hasLength(payloadFile) && StringUtils.hasLength(variableNames)) {
			request.setPayloadFile(uri.getPath() + payloadFile);
			request.setVariableNames(variableNames);
		}

		String payloadId = snapshot.getPayloadId();
		if (StringUtils.hasLength(payloadId)) {
			request.setPayloadUrl(getPayloadApiUrl(payloadId));
			if (StringUtils.hasLength(variableNames)) {
				request.setVariableNames(variableNames);
			} else {
				Set<String> variables = new HashSet<>(ValueReplacer.readReplaceableKeys(snapshot.getUrl()));
				String[] headers = snapshot.getHeaders();
				if (headers != null) {
					for (String header : snapshot.getHeaders()) {
						variables.addAll(ValueReplacer.readReplaceableKeys(header));
					}
				}
				if (snapshot.getBody() != null) {
					variables.addAll(ValueReplacer.readReplaceableKeys(snapshot.getBody()));
				}
				request.setVariableNames(String.join(",", variables));
			}
		}
		return request;
	}

}
