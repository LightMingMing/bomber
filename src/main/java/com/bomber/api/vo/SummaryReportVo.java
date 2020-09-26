package com.bomber.api.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryReportVo {
	private int threads;
	private double tps;
	private double min;
	private double p25;
	private double p50;
	private double p75;
	private double p90;
	private double p95;
	private double p99;
	private double max;
}