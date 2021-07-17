package com.bomber.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 概要报告
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class SummaryReport extends BaseEntity<Long> {

	private static final long serialVersionUID = -5311538623696862161L;

	private Long testingRecordId;

	/**
	 * 并发数
	 */
	private int numberOfThreads;

	/**
	 * 请求数
	 */
	private int numberOfRequests;

	private double tps;

	/**
	 * 平均响应时间
	 */
	private double avg;

	/**
	 * 最短响应时间
	 */
	private double min;

	/**
	 * 最长响应时间
	 */
	private double max;

	/**
	 * 25% 请求的响应时间
	 */
	private double top25;

	/**
	 * 50% 请求的响应时间
	 */
	private double top50;

	/**
	 * 75% 请求的响应时间
	 */
	private double top75;

	/**
	 * 90% 请求的响应时间
	 */
	private double top90;

	/**
	 * 95% 请求的响应时间
	 */
	private double top95;

	/**
	 * 99% 请求的响应时间
	 */
	private double top99;

	/**
	 * 标准方差
	 */
	private double stdDev;

	private int req1xx;

	private int req2xx;

	private int req3xx;

	private int req4xx;

	private int req5xx;

	private int other;

	private int errorCount;

	private Date startTime;

	private Date endTime;

	public int getSuccessCount() {
		return errorCount > 0 ? numberOfRequests - errorCount : getReq2xx() + getReq3xx();
	}

	public int getFailureCount() {
		return errorCount > 0 ? errorCount : getReq1xx() + getReq4xx() + getReq5xx() + getOther();
	}

	public double getSuccessRate() {
		return getSuccessCount() / (double) numberOfRequests;
	}

	public double getFailureRate() {
		return getFailureCount() / (double) numberOfRequests;
	}
}
