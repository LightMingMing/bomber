package com.bomber.model;

import static com.bomber.model.BombingRecord.ACTION_COLUMN_BUTTONS;
import static com.bomber.model.BombingRecord.BOTTOM_BUTTONS;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bombing_record")
@Richtable(showQueryForm = true, celleditable = false, downloadable = false, order = "startTime desc", actionColumnButtons = ACTION_COLUMN_BUTTONS, bottomButtons = BOTTOM_BUTTONS)
public class BombingRecord extends BaseEntity {

	protected static final String ACTION_COLUMN_BUTTONS = "<@btn view='view'/><@btn view='input' label='edit'/>"
			+ "<a href='<@url value='/bombingRecord/displayChart?recordId='/>${(entity.id)!}' target='_blank' class='btn'>chart</a>";

	protected static final String BOTTOM_BUTTONS = "<button type='button' class='btn' data-shown='selected' onclick=\"redirectTo('<@url value='/bombingRecord/compare?recordIds='/>' + checkedIds())\">${getText('compare')}</button>"
			+ "<@btn action='delete' confirm=true/> <@btn class='reload'/> <@btn class='filter'/>";

	private static final String STATUS_TEMPLATE = "<span class='label label-<#if value.name()=='NEW'>info<#elseif value.name()=='RUNNING'>warning<#elseif value.name()=='COMPLETED'>success<#else>error</#if>'>${value}</span>";

	private static final String CENTER_ATTRIBUTE = "{\"style\":\"text-align: center\"}";

	private static final long serialVersionUID = -7435247147997107193L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "httpSampleId", nullable = false)
	@UiConfig(alias = "HTTP请求", width = "200px", template = "<#if value?has_content>${value.name}</#if>", readonly = @Readonly(true), shownInPick = true)
	private HttpSample httpSample;

	@Column(nullable = false)
	@UiConfig(alias = "记录名", width = "200px")
	private String name;

	@Column(nullable = false)
	@UiConfig(alias = "线程组", excludedFromQuery = true, readonly = @Readonly(true))
	private List<Integer> threadGroup;

	@Min(1)
	@UiConfig(alias = "请求数 / 线程", width = "150px", excludedFromQuery = true, readonly = @Readonly(true), cellDynamicAttributes = CENTER_ATTRIBUTE)
	private int requestsPerThread;

	@UiConfig(alias = "状态", width = "150px", readonly = @Readonly(true), template = STATUS_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private BombingStatus status;

	@UiConfig(alias = "开始时间", width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date startTime;

	@UiConfig(alias = "结束时间", width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date endTime;

	@Column(length = 1024)
	@UiConfig(alias = "备注", type = "textarea", hiddenInList = @Hidden(true), excludedFromQuery = true)
	private String remark;
}
