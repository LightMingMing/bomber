package com.bomber.model;

import static com.bomber.model.BombingRecord.ACTION_COLUMN_BUTTONS;
import static com.bomber.model.BombingRecord.BOTTOM_BUTTONS;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import com.bomber.engine.Scope;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bombing_record")
@Richtable(showQueryForm = true, celleditable = false, downloadable = false, order = "createTime desc", actionColumnButtons = ACTION_COLUMN_BUTTONS, bottomButtons = BOTTOM_BUTTONS)
public class BombingRecord extends BaseEntity {

	protected static final String ACTION_COLUMN_BUTTONS = "<@btn view='view'/><@btn view='input' label='edit'/>"
			+ "<a href='<@url value='/summaryReport?bombingRecord='/>${(entity.id)!}' rel='richtable' class='btn'>${getText('summaryReport')}</a>"
			+ "<a href='<@url value='/bombingRecord/displayChartByG2?recordId='/>${(entity.id)!}' target='_blank' class='btn'>${getText('chart')}</a>"
			+ "<#if (entity.status.name() == 'RUNNING')><@btn action='pauseExecute' label='pause'/></#if>"
			+ "<#if (entity.status.name() == 'PAUSE' || entity.status.name() == 'FAILURE')><@btn action='continueExecute' label='continue'/></#if>";

	protected static final String BOTTOM_BUTTONS = "<button type='button' class='btn' data-shown='selected' onclick=\"redirectTo('<@url value='/bombingRecord/compareByG2?recordIds='/>' + checkedIds())\">${getText('compare')}</button>"
			+ "<@btn action='delete' confirm=true/> <@btn class='reload'/> <@btn class='filter'/>";

	private static final String THREAD_GROUPS_TEMPLATE = "<#list value as threads>"
			+ "<#if (entity.status.name() == 'COMPLETED' || threads_index != entity.threadGroupCursor)><span class='label'>${threads}</span><#sep> "
			+ "<#elseif (entity.status.name() == 'READY')><span class='label label-info'>${threads}</span><#sep> "
			+ "<#elseif (entity.status.name() == 'RUNNING')><span class='label label-warning'>${threads}</span><#sep> "
			+ "<#elseif (entity.status.name() == 'PAUSE')><span class='label label-inverse'>${threads}</span><#sep> "
			+ "<#else><span class='label label-important'>${threads}</span><#sep> </#if></#list>";

	private static final String STATUS_TEMPLATE = "<span class='label <#switch value.name()>"
			+ "<#case 'COMPLETED'>label-success<#break><#case 'FAILURE'>label-important<#break>"
			+ "<#case 'RUNNING'>label-warning<#break><#case 'PAUSE'>label-inverse<#break>"
			+ "<#default>label-info</#switch> '>${value}</span>";

	private static final String CENTER_ATTRIBUTE = "{\"style\":\"text-align: center\"}";

	private static final long serialVersionUID = -7435247147997107193L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@UiConfig(width = "200px", template = "<#if value?has_content>${value.name}</#if>", readonly = @Readonly(true), shownInPick = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private HttpSample httpSample;

	@Column(nullable = false)
	@UiConfig(alias = "recordName", width = "200px", cellDynamicAttributes = CENTER_ATTRIBUTE)
	private String name;

	@Column(nullable = false)
	@UiConfig(template = THREAD_GROUPS_TEMPLATE, excludedFromQuery = true, readonly = @Readonly(true), cellDynamicAttributes = "{\"style\":\"min-width:200px\"}")
	private List<Integer> threadGroups;

	@UiConfig(hidden = true)
	private int threadGroupCursor = 0;

	@UiConfig(hidden = true)
	private int activeThreads = 0;

	@UiConfig(hidden = true)
	private Scope scope;

	@UiConfig(hiddenInList = @Hidden(true), readonly = @Readonly(true), excludedFromQuery = true)
	private int beginUserIndex = 0;

	@UiConfig(hiddenInList = @Hidden(true), readonly = @Readonly(true), excludedFromQuery = true)
	private int iterations;

	@UiConfig(hiddenInList = @Hidden(true), readonly = @Readonly(true), excludedFromQuery = true)
	private int completedIterations;

	@Min(1)
	@UiConfig(width = "150px", excludedFromQuery = true, readonly = @Readonly(true), cellDynamicAttributes = CENTER_ATTRIBUTE)
	private int requestsPerThread;

	@UiConfig(width = "150px", readonly = @Readonly(true), template = STATUS_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private BombingStatus status;

	@UiConfig(hiddenInList = @Hidden(true), readonly = @Readonly(true))
	private Date createTime;

	@UiConfig(width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date startTime;

	@UiConfig(width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date endTime;

	@Column(length = 1024)
	@UiConfig(type = "textarea", hiddenInList = @Hidden(true), excludedFromQuery = true)
	private String remark;
}
