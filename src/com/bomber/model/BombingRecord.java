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
			+ "<a href='<@url value='/bombingRecord/displayChart?recordId='/>${(entity.id)!}' target='_blank' class='btn'>${getText('chart')}</a>"
			+ "<#if (entity.status.name() == 'RUNNING')><@btn action='pauseExecute' label='pause'/></#if>"
			+ "<#if (entity.status.name() == 'PAUSE')><@btn action='continueExecute' label='continue'/></#if>";

	protected static final String BOTTOM_BUTTONS = "<button type='button' class='btn' data-shown='selected' onclick=\"redirectTo('<@url value='/bombingRecord/compare?recordIds='/>' + checkedIds())\">${getText('compare')}</button>"
			+ "<@btn action='delete' confirm=true/> <@btn class='reload'/> <@btn class='filter'/>";

	private static final String THREAD_GROUP_TEMPLATE = "<#list value as threads>"
			+ "<#if (entity.status.name() == 'READY' || entity.status.name() == 'COMPLETED')><span class='label'>${threads}</span><#sep> "
			+ "<#elseif (threads == entity.activeThreads)>"
			+ "<#if (entity.status.name() == 'RUNNING')><span class='label label-warning'>${threads}</span><#sep> </#if>"
			+ "<#if (entity.status.name() == 'FAILURE')><span class='label label-important'>${threads}</span><#sep> </#if>"
			+ "<#if (entity.status.name() == 'PAUSE')><span class='label label-inverse'>${threads}</span><#sep> </#if>"
			+ "<#else><span class='label'>${threads}</span><#sep> </#if></#list>";

	private static final String STATUS_TEMPLATE = "<span class='label <#switch value.name()>"
			+ "<#case 'COMPLETED'>label-success<#break>" + "<#case 'FAILURE'>label-important<#break>"
			+ "<#case 'RUNNING'>label-warning<#break>" + "<#case 'PAUSE'>label-inverse<#break>"
			+ "<#default>label-info</#switch> '>${value}</span>";

	private static final String CENTER_ATTRIBUTE = "{\"style\":\"text-align: center\"}";

	private static final long serialVersionUID = -7435247147997107193L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "httpSampleId", nullable = false)
	@UiConfig(width = "200px", template = "<#if value?has_content>${value.name}</#if>", readonly = @Readonly(true), shownInPick = true)
	private HttpSample httpSample;

	@Column(nullable = false)
	@UiConfig(alias = "recordName", width = "200px")
	private String name;

	@Column(nullable = false)
	@UiConfig(template = THREAD_GROUP_TEMPLATE, excludedFromQuery = true, readonly = @Readonly(true))
	private List<Integer> threadGroup;

	@UiConfig(hidden = true, excludedFromQuery = true, readonly = @Readonly(true))
	private int activeThreads = 0;

	@Min(1)
	@UiConfig(width = "150px", excludedFromQuery = true, readonly = @Readonly(true), cellDynamicAttributes = CENTER_ATTRIBUTE)
	private int requestsPerThread;

	@UiConfig(width = "150px", readonly = @Readonly(true), template = STATUS_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private BombingStatus status;

	@UiConfig(width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date startTime;

	@UiConfig(width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date endTime;

	@Column(length = 1024)
	@UiConfig(type = "textarea", hiddenInList = @Hidden(true), excludedFromQuery = true)
	private String remark;
}
