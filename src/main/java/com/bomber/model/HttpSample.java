package com.bomber.model;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bomber.converter.HttpHeaderListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "http_sample")
@Richtable(showQueryForm = true, celleditable = false, actionColumnButtons = HttpSample.ACTION_COLUMN_BUTTONS, order = "createDate desc")
public class HttpSample extends BaseEntity {

	protected static final String ACTION_COLUMN_BUTTONS = "<@btn view='view'/>"
			+ "<@btn view='input' label='edit' windowoptions='{\"minWidth\":\"750\"}'/>"
			+ "<@btn view='quickCreate' label='clone'/> <@btn action='singleShot'/>"
			+ "<@btn view='bomb' label='bomb' windowoptions='{\"minHeight\":\"200\"}'/>";

	private static final String HEADERS_TEMPLATE = "<#if value?has_content><#list value as header><span style='color:#d73a49;font-weight:bold'>${header.name}:</span> ${header.value}<#sep><br></#list></#if>";

	private static final String BODY_INPUT_TEMPLATE = "<textarea id='httpSample-body' name='httpSample.body' "
			+ "class='input-xxlarge' style='min-height: 200px'>${(entity.body)!}</textarea>";

	private static final String CODE_ATTRIBUTE = "{\"style\":\"font-family:SFMono-Regular,Consolas,Liberation Mono,Menlo,monospace\"}";

	private static final long serialVersionUID = 5801606517538547923L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "applicationInstanceId", nullable = false)
	@UiConfig(width = "200px", shownInPick = true, cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private ApplicationInstance applicationInstance;

	@Column(nullable = false)
	@UiConfig(alias = "requestName", width = "200px", cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private String name;

	@Column(nullable = false)
	@UiConfig(alias = "requestMethod", width = "100px", cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private RequestMethod method;

	@Column(nullable = false)
	@UiConfig(cssClass = "input-xxlarge", shownInPick = true, cellDynamicAttributes = "{\"style\":\"font-family:SFMono-Regular,Consolas,Liberation Mono,Menlo,monospace;min-width:200px\"}")
	private String path;

	@UiConfig(alias = "headers", width = "250px", listTemplate = HEADERS_TEMPLATE, cellDynamicAttributes = CODE_ATTRIBUTE)
	@Convert(converter = HttpHeaderListConverter.class)
	private List<HttpHeader> headers;

	@Column(length = 10240)
	@UiConfig(hiddenInList = @Hidden(true), type = "textarea", inputTemplate = BODY_INPUT_TEMPLATE, excludedFromQuery = true)
	private String body;

	@Transient
	@UiConfig(alias = "file", hiddenInList = @Hidden(true), hiddenInView = @Hidden(true))
	private File csvFile;

	@Transient
	@UiConfig(hidden = true)
	private String csvFileFileName;

	@UiConfig(alias = "filePath", hiddenInList = @Hidden(true), readonly = @Readonly(true), excludedFromQuery = true)
	private String csvFilePath;

	@UiConfig(alias = "variableNames", width = "180px", description = "separatedByCommas", excludedFromQuery = true)
	private String variableNames;

	@JsonIgnore
	@CreationTimestamp
	@Column(updatable = false)
	@UiConfig(hiddenInList = @Hidden(true), hiddenInInput = @Hidden(true), excludedFromQuery = true)
	private Date createDate;

	@JsonIgnore
	@UpdateTimestamp
	@Column(insertable = false)
	@UiConfig(hiddenInList = @Hidden(true), hiddenInInput = @Hidden(true), excludedFromQuery = true)
	private Date modifyDate;
}
