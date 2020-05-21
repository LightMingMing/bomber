package com.bomber.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import com.bomber.converter.LatencyStatsConverter;
import com.bomber.converter.StatusStatsConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "summary_report")
@Richtable(showQueryForm = true, celleditable = false, order = "startTime desc", actionColumnButtons = "<@btn view='view'/>", bottomButtons = "<@btn action='delete' confirm=true/> <@btn class='reload'/> <@btn class='filter'/>")
public class SummaryReport extends BaseEntity {

	private static final long serialVersionUID = 273435417896067478L;

	private static final String PROGRESS_TEMPLATE = ""
			+ "<div class='progress' style='white-space:nowrap;height:30px;margin-bottom:0px'>"
			+ "  <div class='bar bar-success' style='width: ${value*100}%;line-height:30px'><#if (value>0.05)>${value*100}%</#if></div>"
			+ "  <div class='bar bar-danger' style='width: ${100-value*100}%;line-height:30px'><#if (value<=0.95)>${100-value*100}%</#if></div>"
			+ "</div>";

	private static final String TIME_UNIT_TEMPLATE = "<#if (value > 999)>${(value/1000.0)?string('#.##')}s<#else>${value?string('#')}ms</#if>";

	private static final String CENTER_ATTRIBUTE = "{\"style\":\"text-align: center\"}";

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bombingRecordId", nullable = false)
	@UiConfig(alias = "name", width = "200px", template = "${(value.httpSample.name)!}-${(value.name)!}")
	private BombingRecord bombingRecord;

	@Min(1)
	@UiConfig(alias = "并发数", width = "50px", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private int numberOfThreads;

	@Min(1)
	@UiConfig(alias = "请求数", width = "50px", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private int numberOfRequests;

	@UiConfig(alias = "TPS", width = "50px", excludedFromQuery = true, description = "每秒请求数", cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double tps;

	@Column(nullable = false)
	@UiConfig(hiddenInList = @Hidden(true), excludedFromQuery = true)
	@Convert(converter = StatusStatsConverter.class)
	private StatusStats statusStats;

	@Column(nullable = false)
	@UiConfig(hiddenInList = @Hidden(true), excludedFromQuery = true)
	@Convert(converter = LatencyStatsConverter.class)
	private LatencyStats latencyStats;

	@Transient
	@UiConfig(alias = "平均响应时间", width = "100px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double avg;

	@Transient
	@UiConfig(alias = "50%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point50;

	@Transient
	@UiConfig(alias = "75%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point75;

	@Transient
	@UiConfig(alias = "90%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point90;

	@Transient
	@UiConfig(alias = "95%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point95;

	@Transient
	@UiConfig(alias = "99%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point99;

	@Transient
	@UiConfig(alias = "标准差", width = "80px", excludedFromQuery = true, hiddenInView = @Hidden(true), description = "响应时间离散程度", cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double stdDev;

	@Column(nullable = false)
	@UiConfig(width = "150px", queryWithRange = true)
	private Date startTime;

	@Column(nullable = false)
	@UiConfig(width = "150px", queryWithRange = true)
	private Date endTime;

	@Transient
	@UiConfig(alias = "成功率/失败率", excludedFromQuery = true, hiddenInView = @Hidden(true), template = PROGRESS_TEMPLATE)
	private double successRate;

	public double getAvg() {
		return latencyStats.getAvg();
	}

	public double getPoint50() {
		return latencyStats.getPercentiles().getPoint50();
	}

	public double getPoint75() {
		return latencyStats.getPercentiles().getPoint75();
	}

	public double getPoint90() {
		return latencyStats.getPercentiles().getPoint90();
	}

	public double getPoint95() {
		return latencyStats.getPercentiles().getPoint95();
	}

	public double getPoint99() {
		return latencyStats.getPercentiles().getPoint99();
	}

	public double getStdDev() {
		return latencyStats.getStdDev();
	}

	public double getSuccessCount() {
		return statusStats.getReq2xx() + statusStats.getReq3xx();
	}

	public double getSuccessRate() {
		return getSuccessCount() / (double) numberOfRequests;
	}

	public double getFailureRate() {
		return 1 - getSuccessRate();
	}
}
