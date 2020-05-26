package com.bomber.engine;

import static com.bomber.converter.HttpHeaderListConverter.convertToString;
import static com.bomber.model.BombingStatus.COMPLETED;
import static com.bomber.model.BombingStatus.NEW;
import static com.bomber.model.BombingStatus.RUNNING;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import com.bomber.service.BombardierRequest;
import com.bomber.service.BombardierResponse;
import com.bomber.service.BombardierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.HttpSampleManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.HttpSample;
import com.bomber.model.SummaryReport;

@Service
public class BomberEngineImpl implements BomberEngine {

	private final HttpSampleManager httpSampleManager;

	private final BombingRecordManager bombingRecordManager;

	private final SummaryReportManager summaryReportManager;

	private final ExecutorService bombingExecutor;

	private final BombardierService bombardierService;

	@Value("${fileStorage.uri:file:///${app.context}/assets/}")
	protected URI uri;

	public BomberEngineImpl(HttpSampleManager httpSampleManager, BombingRecordManager bombingRecordManager,
			SummaryReportManager summaryReportManager, BombardierService bombardierService,
			ExecutorService bombingExecutor) {
		this.httpSampleManager = httpSampleManager;
		this.bombingRecordManager = bombingRecordManager;
		this.summaryReportManager = summaryReportManager;
		this.bombardierService = bombardierService;
		this.bombingExecutor = bombingExecutor;
	}

	@Override
	@Transactional
	public void execute(@NonNull BomberPlan bomberPlan) {
		HttpSample httpSample = httpSampleManager.get(bomberPlan.getSampleId());
		if (httpSample == null) {
			throw new IllegalArgumentException("httpSample does not exist");
		}

		BombingRecord record = new BombingRecord();
		record.setName(bomberPlan.getName());
		record.setThreadGroup(bomberPlan.getThreadGroup());
		record.setRequestsPerThread(bomberPlan.getRequestsPerThread());
		record.setHttpSample(httpSample);
		record.setStartTime(new Date());
		record.setStatus(NEW);

		bombingRecordManager.save(record);

		String recordId = record.getId();

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				bombingExecutor.execute(() -> doExecute(recordId, bomberPlan));
			}
		});
	}

	public void doExecute(String recordId, BomberPlan bomberPlan) {
		HttpSample httpSample = httpSampleManager.get(bomberPlan.getSampleId());

		BombingRecord record = bombingRecordManager.get(recordId);
		record.setStatus(RUNNING);
		bombingRecordManager.save(record);

		int requestCount = 0;
		BombardierRequest request = new BombardierRequest(httpSample.getUrl(), httpSample.getMethod(),
				convertToString(httpSample.getHeaders()), httpSample.getBody(),
				uri.getPath() + httpSample.getCsvFilePath(), httpSample.getVariableNames());

		for (int numberOfThreads : bomberPlan.getThreadGroup()) {
			Date startTime = new Date();
			int numberOfRequests = numberOfThreads * bomberPlan.getRequestsPerThread();
			request.setNumberOfConnections(numberOfThreads);
			request.setNumberOfRequests(numberOfRequests);
			request.setStartLine(requestCount);
			BombardierResponse response = bombardierService.execute(request);

			SummaryReport summaryReport = convertToSummaryReport(response);
			summaryReport.setBombingRecord(record);
			summaryReport.setStartTime(startTime);
			summaryReport.setEndTime(new Date());
			summaryReportManager.save(summaryReport);

			requestCount += numberOfRequests;
		}

		record.setStatus(COMPLETED);
		record.setEndTime(new Date());
		bombingRecordManager.save(record);
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
}
