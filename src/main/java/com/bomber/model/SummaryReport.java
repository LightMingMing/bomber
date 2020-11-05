package com.bomber.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AutoConfig
@Table(name = "summary_report")
@Richtable(showQueryForm = true, celleditable = false, order = "startTime desc", actionColumnButtons = "<@btn view='view'/>", bottomButtons = "<@btn action='delete' confirm=true/> <@btn class='reload'/> <@btn class='filter'/>")
public class SummaryReport extends BaseEntity {

	private static final long serialVersionUID = 273435417896067478L;

	private static final String PROGRESS_TEMPLATE = ""
			+ "<div class='progress' style='white-space:nowrap;height:30px;margin-bottom:0px'>"
			+ "  <div class='bar bar-success' style='width: ${value*100}%;line-height:30px'><#if (value>0.05)>${value*100}%</#if></div>"
			+ "  <div class='bar bar-danger' style='width: ${100-value*100}%;line-height:30px'><#if (value<=0.95)>${100-value*100}%</#if></div>"
			+ "</div>";

	private static final String TIME_UNIT_TEMPLATE = "<#if (value>999)>${(value/1000.0)?string('#.##')}s<#else>${value?string('#')}ms</#if>";

	private static final String CENTER_ATTRIBUTE = "{\"style\":\"text-align: center\"}";

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@UiConfig(alias = "recordName", width = "200px", template = "${(value.httpSample.name)!}-${(value.name)!}", csvTemplate = "${(value.httpSample.name)!} - ${(value.name)!}", cellDynamicAttributes = CENTER_ATTRIBUTE)
	private BombingRecord bombingRecord;

	@Min(1)
	@UiConfig(alias = "threads", width = "70px", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private int numberOfThreads;

	@Min(1)
	@UiConfig(alias = "requests", width = "70px", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private int numberOfRequests;

	@UiConfig(alias = "TPS", width = "80px", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double tps;

	@UiConfig(width = "100px", excludedFromQuery = true, template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double avg;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true), template = TIME_UNIT_TEMPLATE)
	private double min;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true), template = TIME_UNIT_TEMPLATE)
	private double max;

	@UiConfig(alias = "25%", width = "50px", excludedFromQuery = true, template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point25;

	@UiConfig(alias = "50%", width = "50px", excludedFromQuery = true, template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point50;

	@UiConfig(alias = "75%", width = "50px", excludedFromQuery = true, template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point75;

	@UiConfig(alias = "90%", width = "50px", excludedFromQuery = true, template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point90;

	@UiConfig(alias = "95%", width = "50px", excludedFromQuery = true, template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point95;

	@UiConfig(alias = "99%", width = "50px", excludedFromQuery = true, template = TIME_UNIT_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double point99;

	@UiConfig(width = "80px", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private double stdDev;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true))
	private int req1xx;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true))
	private int req2xx;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true))
	private int req3xx;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true))
	private int req4xx;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true))
	private int req5xx;

	@UiConfig(excludedFromQuery = true, hiddenInList = @Hidden(true))
	private int other;

	@UiConfig(description = "errorCount.desc", excludedFromQuery = true, hiddenInList = @Hidden(true))
	private int errorCount;

	@Column(nullable = false)
	@UiConfig(width = "150px", queryWithRange = true)
	private Date startTime;

	@Column(nullable = false)
	@UiConfig(width = "150px", queryWithRange = true)
	private Date endTime;

	@Transient
	@UiConfig(excludedFromQuery = true, hiddenInView = @Hidden(true), template = PROGRESS_TEMPLATE)
	private double successRate;

	public double getSuccessCount() {
		return errorCount > 0 ? numberOfRequests - errorCount : getReq2xx() + getReq3xx();
	}

	public double getFailureCount() {
		return errorCount > 0 ? errorCount : getReq1xx() + getReq4xx() + getReq5xx() + getOther();
	}

	public double getSuccessRate() {
		return getSuccessCount() / (double) numberOfRequests;
	}

	public double getFailureRate() {
		return getFailureCount() / (double) numberOfRequests;
	}
}
