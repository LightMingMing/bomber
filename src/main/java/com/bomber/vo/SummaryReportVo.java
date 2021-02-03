package com.bomber.vo;

import com.bomber.model.SummaryReport;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SummaryReportVo {
	private int threads;
	private double tps;
	private double avg;
	private double min;
	private double p25;
	private double p50;
	private double p75;
	private double p90;
	private double p95;
	private double p99;
	private double max;
	private Date startTime;

	public static SummaryReportVo map(SummaryReport report) {
		SummaryReportVo reportVo = new SummaryReportVo();
		reportVo.setThreads(report.getNumberOfThreads());
		reportVo.setTps(report.getTps());
		reportVo.setAvg(report.getAvg());
		reportVo.setMin(report.getMin());
		reportVo.setP25(report.getPoint25());
		reportVo.setP50(report.getPoint50());
		reportVo.setP75(report.getPoint75());
		reportVo.setP90(report.getPoint90());
		reportVo.setP95(report.getPoint95());
		reportVo.setP99(report.getPoint99());
		reportVo.setMax(report.getMax());
		reportVo.setStartTime(report.getStartTime());
		return reportVo;
	}
}