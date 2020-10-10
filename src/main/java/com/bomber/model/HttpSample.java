package com.bomber.model;

import static com.bomber.util.ValueReplacer.isReplaceable;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;
import org.springframework.http.HttpMethod;

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
			+ "<@btn view='quickCreate' label='copy'/> <@btn view='singleShotV2' label='singleShot'/>"
			+ "<@btn view='bomb' label='bomb' windowoptions='{\"minHeight\":\"200\"}'/>"
			+ "<a href='<@url value='/bombingRecord?httpSample='/>${(entity.id)!}' rel='richtable' class='btn'>${getText('bombingRecord')}</a>";

	private static final String HEADERS_TEMPLATE = "<#if value?has_content><#list value as header><span style='color:#d73a49;font-weight:bold'>${header.name}:</span> ${header.value}<#sep><br></#list></#if>";

	private static final String BODY_INPUT_TEMPLATE = "<textarea id='httpSample-body' name='httpSample.body' "
			+ "class='input-xxlarge' style='height: 350px'>${(entity.body)!}</textarea>";

	private static final String BODY_VIEW_TEMPLATE = "<#if entity.body?has_content><code class='block json' style='color:green;max-height:350px;overflow-y: auto'>${entity.body?no_esc}</code></#if>";

	private static final String CODE_ATTRIBUTE = "{\"style\":\"font-family:SFMono-Regular,Consolas,Liberation Mono,Menlo,monospace\"}";

	private static final long serialVersionUID = 5801606517538547923L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "applicationInstanceId", nullable = false)
	@UiConfig(width = "200px", shownInPick = true, cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private ApplicationInstance applicationInstance;

	@Column(nullable = false)
	@UiConfig(alias = "requestName", width = "200px", cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private String name;

	@Column
	@UiConfig(templateName = "httpSample_tag", type = "dictionary", cssClass = "chosen", hiddenInList = @Hidden(true), excludedFromQuery = true)
	private Set<String> tags;

	@Column(nullable = false)
	@UiConfig(alias = "requestMethod", width = "100px", cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private HttpMethod method;

	@Column(nullable = false)
	@UiConfig(cssClass = "input-xxlarge", shownInPick = true, excludedFromQuery = true, cellDynamicAttributes = "{\"style\":\"font-family:SFMono-Regular,Consolas,Liberation Mono,Menlo,monospace;min-width:200px\"}")
	private String path;

	@UiConfig(alias = "headers", width = "250px", listTemplate = HEADERS_TEMPLATE, cellDynamicAttributes = CODE_ATTRIBUTE)
	@Convert(converter = HttpHeaderListConverter.class)
	private List<HttpHeader> headers;

	@Column(length = 10240)
	@UiConfig(hiddenInList = @Hidden(true), type = "textarea", viewTemplate = BODY_VIEW_TEMPLATE, inputTemplate = BODY_INPUT_TEMPLATE, excludedFromQuery = true)
	private String body;

	@Transient
	@UiConfig(alias = "file", hiddenInList = @Hidden(true), hiddenInView = @Hidden(true))
	private File csvFile;

	@Transient
	@UiConfig(hidden = true)
	private String csvFileFileName;

	@UiConfig(alias = "filePath", hiddenInList = @Hidden(true), hiddenInInput = @Hidden(expression = "!value?has_content"), excludedFromQuery = true)
	private String csvFilePath;

	@UiConfig(hiddenInList = @Hidden(true), description = "separatedByCommas", excludedFromQuery = true)
	private String variableNames;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payloadId")
	@UiConfig(alias = "payload", hiddenInList = @Hidden(true), excludedFromQuery = true)
	private Payload payload;

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

	public String getUrl() {
		Objects.requireNonNull(applicationInstance, "applicationInstance");
		return applicationInstance.getUrl() + (path.startsWith("/") ? path : "/" + path);
	}

	public boolean isMutable() {
		return isReplaceable(path) || isReplaceable(body)
				|| headers.stream().anyMatch(header -> isReplaceable(header.getValue()));
	}
}
