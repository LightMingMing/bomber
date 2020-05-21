package com.bomber.model;

import java.util.Date;

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
@Richtable(showQueryForm = true, celleditable = false, order = "startTime desc", actionColumnButtons = "<@btn view='view'/><@btn view='input' label='edit'/>", bottomButtons = "<@btn action='delete' confirm=true/> <@btn class='reload'/> <@btn class='filter'/>")
public class BombingRecord extends BaseEntity {

	private static final long serialVersionUID = -7435247147997107193L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "httpSampleId", nullable = false)
	@UiConfig(alias = "HTTP请求", width = "200px", template = "<#if value?has_content>${value.name}</#if>", readonly = @Readonly(true))
	private HttpSample httpSample;

	@Column(nullable = false)
	@UiConfig(width = "200px")
	private String name;

	@Column(nullable = false)
	@UiConfig(alias = "线程组", excludedFromQuery = true, readonly = @Readonly(true))
	private String threadGroup;

	@Min(1)
	@UiConfig(alias = "请求数 / 线程", width = "150px", excludedFromQuery = true, readonly = @Readonly(true))
	private int requestsPerThread;

	@UiConfig(alias = "状态", width = "150px", readonly = @Readonly(true))
	private BombingStatus status;

	@UiConfig(alias = "开始时间", width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date startTime;

	@UiConfig(alias = "结束时间", width = "150px", queryWithRange = true, readonly = @Readonly(true))
	private Date endTime;

	@Column(length = 1024)
	@UiConfig(alias = "备注", type = "textarea", hiddenInList = @Hidden(true), excludedFromQuery = true)
	private String remark;
}
