package com.bomber.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AutoConfig
@Table(name = "application_instance")
@Richtable(showQueryForm = true, celleditable = false, order = "appName asc", actionColumnButtons = "<@btn view='view'/><@btn view='input' label='edit'/> <@btn view='quickCreate' label='copy'/>")
public class ApplicationInstance extends BaseEntity {

	private static final long serialVersionUID = -7191653658982379180L;

	private static final String CENTER_ATTRIBUTE = "{\"style\":\"text-align: center\"}";

	private static final String ENV_TEMPLATE = "<#if value?has_content><span class='label <#switch value.name()>"
			+ "<#case 'test'>label-inverse<#break><#case 'prod'>label-important<#break>"
			+ "<#case 'bbit'>label-info<#break><#case 'sit'>label-warning<#break>"
			+ "<#case 'uat'>label-success<#break><#default></#switch> '>${value}</span></#if>";

	@Column(length = 32, nullable = false)
	@UiConfig(shownInPick = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private String appName;

	@Column(nullable = false)
	@UiConfig(shownInPick = true, template = "<a href='${value}' target='_blank'>${value}</a>", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private String url;

	@Column(length = 16)
	@UiConfig(shownInPick = true, template = ENV_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private Env env;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	@UiConfig(excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private Date createDate;

	@Version
	private int version = -1;

	@Override
	public String toString() {
		return appName + (env == null ? "" : "[" + env + "]");
	}
}
