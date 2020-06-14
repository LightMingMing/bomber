package com.bomber.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
@Richtable(showQueryForm = true, celleditable = false, order = "appName asc", actionColumnButtons = "<@btn view='view'/><@btn view='input' label='edit'/> <@btn view='quickCreate' label='clone'/>")
public class ApplicationInstance extends BaseEntity {

	private static final long serialVersionUID = -7191653658982379180L;

	private static final String CENTER_ATTRIBUTE = "{\"style\":\"text-align: center\"}";

	private static final String ENV_TEMPLATE = "<#if value?has_content><spa n class='label <#switch value.name()>"
			+ "<#case 'test'>label-inverse<#break><#case 'prod'>label-important<#break>"
			+ "<#case 'bbit'>label-info<#break><#case 'sit'>label-warning<#break>"
			+ "<#case 'uat'>label-success<#break><#default></#switch> '>${value}</span></#if>";

	@Column(length = 32, nullable = false)
	@UiConfig(width = "200px", shownInPick = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private String appName;

	@Column(length = 8, nullable = false)
	@UiConfig(width = "150px", shownInPick = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private Protocol protocol = Protocol.http;

	@Column(length = 32, nullable = false)
	@UiConfig(width = "200px", shownInPick = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private String host;

	@Min(0)
	@Max(65535)
	@Column(nullable = false)
	@UiConfig(width = "150px", shownInPick = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private Integer port;

	@Column(length = 16)
	@UiConfig(width = "150px", shownInPick = true, template = ENV_TEMPLATE, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private Env env;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	@UiConfig(width = "250px", excludedFromQuery = true, cellDynamicAttributes = CENTER_ATTRIBUTE)
	private Date createDate;

	@Version
	private int version = -1;

	@Override
	public String toString() {
		return appName + '@' + host + ':' + port + (env == null ? "" : "[" + env + "]");
	}
}
