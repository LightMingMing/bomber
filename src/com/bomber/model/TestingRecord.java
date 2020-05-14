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

import org.hibernate.annotations.CreationTimestamp;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import com.bomber.converter.LatencyStatsConverter;
import com.bomber.converter.StatusStatsConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "testing_record")
@Richtable(showQueryForm = true, celleditable = false, order = "createDate desc", actionColumnButtons = "<@btn view='view'/>", bottomButtons = "<@btn action='delete' confirm=true/> <@btn class='reload'/> <@btn class='filter'/>")
public class TestingRecord extends BaseEntity {

	private static final String PROGRESS_TEMPLATE = ""
			+ "<div class='progress' style='white-space:nowrap;height:30px;margin-bottom:0px'>"
			+ "  <div class='bar bar-success' style='width: ${value*100}%;line-height:30px'><#if (value>0.05)>${value*100}%</#if></div>"
			+ "  <div class='bar bar-danger' style='width: ${100-value*100}%;line-height:30px'><#if (value<=0.95)>${100-value*100}%</#if></div>"
			+ "</div>";

	private static final String MILLISECOND_UNIT_TEMPLATE = "${value}ms";

	@UiConfig(alias = "Http请求", width = "150px", template = "<#if value?has_content>${value.name}</#if>")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "httpSample")
	private HttpSample httpSample;

	@UiConfig(alias = "并发数", width = "50px", excludedFromQuery = true)
	private int numberOfThreads;

	@UiConfig(alias = "请求数", width = "50px", excludedFromQuery = true)
	private int numberOfRequests;

	@UiConfig(alias = "TPS", width = "50px", excludedFromQuery = true, description = "每秒请求数")
	private double tps;

	@UiConfig(hiddenInList = @Hidden(true), excludedFromQuery = true)
	@Convert(converter = StatusStatsConverter.class)
	private StatusStats statusStats;

	@UiConfig(hiddenInList = @Hidden(true), excludedFromQuery = true)
	@Convert(converter = LatencyStatsConverter.class)
	private LatencyStats latencyStats;

	@Transient
	@UiConfig(alias = "平均响应时间", width = "100px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = MILLISECOND_UNIT_TEMPLATE)
	private double avg;

	@Transient
	@UiConfig(alias = "50%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = MILLISECOND_UNIT_TEMPLATE)
	private double point50;

	@Transient
	@UiConfig(alias = "75%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = MILLISECOND_UNIT_TEMPLATE)
	private double point75;

	@Transient
	@UiConfig(alias = "90%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = MILLISECOND_UNIT_TEMPLATE)
	private double point90;

	@Transient
	@UiConfig(alias = "95%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = MILLISECOND_UNIT_TEMPLATE)
	private double point95;

	@Transient
	@UiConfig(alias = "99%", width = "50px", excludedFromQuery = true, hiddenInView = @Hidden(true), template = MILLISECOND_UNIT_TEMPLATE)
	private double point99;

	@Transient
	@UiConfig(alias = "标准差", width = "80px", excludedFromQuery = true, hiddenInView = @Hidden(true), description = "响应时间离散程度")
	private double stdDev;

	@JsonIgnore
	@CreationTimestamp
	@Column(updatable = false)
	@UiConfig(width = "150px", queryWithRange = true)
	protected Date createDate;

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
