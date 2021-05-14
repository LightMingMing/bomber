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

import com.bomber.engine.converter.BombardierRequestConverter;
import com.bomber.engine.internal.Counter;
import com.bomber.engine.model.BomberContext;
import com.bomber.engine.rpc.BombardierRequest;
import com.bomber.engine.rpc.BombardierResponse;
import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.SummaryReport;
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

	@Override
	public void execute(@NonNull BomberContext ctx) {
		bombingExecutor.execute(() -> {
			registry.register(ctx);
			try {
				doExecute(ctx);
			} finally {
				registry.unregister(ctx);
			}
		});
	}

	@Override
	public void pauseExecute(String id) {
		Optional.ofNullable(registry.get(id)).ifPresent(BomberContext::pause);
	}

	private void doExecute(BomberContext ctx) {
		BombingRecord record = bombingRecordManager.get(ctx.getId());
		if (record == null) {
			return; // is removed
		}
		if (record.getStartTime() == null) {
			record.setStartTime(new Date()); // start time
		}
		record.setStatus(RUNNING);
		bombingRecordManager.save(record);

		BombardierRequest request = BombardierRequestConverter.INSTANCE.convert(ctx);

		Counter counter = ctx.rebuildCounter();

		for (; ctx.hasNextThreadGroup(); ctx.nextThreadGroup()) {

			request.setNumberOfConnections(ctx.getNumberOfThreads());
			request.setNumberOfRequests(ctx.getNumberOfRequests());
			request.setStartLine(counter.getAndCount());

			record.setThreadGroupCursor(ctx.getThreadGroupCursor());
			record.setActiveThreads(ctx.getNumberOfThreads());

			for (; ctx.hasNextIteration(); ctx.nextIteration()) {

				record.setCurrentIterations(ctx.getIteration());

				if (ctx.isPaused()) {
					record.setStatus(PAUSE);
					bombingRecordManager.save(record);
					return;
				}

				bombingRecordManager.save(record);
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
