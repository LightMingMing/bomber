package com.bomber.engine;

import com.bomber.engine.rpc.BombardierResponse;
import com.bomber.entity.SummaryReport;

/**
 * 转化器
 *
 * @author MingMing Zhao
 */
public class SummaryReporterConverter {

	public static final SummaryReporterConverter INSTANCE = new SummaryReporterConverter();

	private SummaryReporterConverter() {
	}

	public SummaryReport convert(BombardierResponse response) {
		SummaryReport report = new SummaryReport();
		report.setNumberOfThreads(response.getNumConns());
		report.setNumberOfRequests(response.getNumReqs());

		BombardierResponse.StatusStats status = response.getStatus();
		report.setReq1xx(status.getReq1xx());
		report.setReq2xx(status.getReq2xx());
		report.setReq3xx(status.getReq3xx());
		report.setReq4xx(status.getReq4xx());
		report.setReq5xx(status.getReq5xx());
		report.setOther(status.getOther());

		BombardierResponse.LatencyStats latency = response.getLatency();
		report.setAvg(latency.getAvg());
		report.setMax(latency.getMax());
		report.setMin(latency.getMin());
		report.setStdDev(latency.getStdDev());

		BombardierResponse.Percentiles percentiles = latency.getPercentiles();
		report.setTop25(percentiles.getPoint25());
		report.setTop50(percentiles.getPoint50());
		report.setTop75(percentiles.getPoint75());
		report.setTop90(percentiles.getPoint90());
		report.setTop95(percentiles.getPoint95());
		report.setTop99(percentiles.getPoint99());

		report.setTps(response.getTps());
		report.setErrorCount(response.getErrorCount());
		return report;
	}
}
