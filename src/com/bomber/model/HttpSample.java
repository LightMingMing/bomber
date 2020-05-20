package com.bomber.model;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import com.bomber.converter.HttpHeaderListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "http_sample")
@Richtable(showQueryForm = true, actionColumnButtons = HttpSample.ACTION_COLUMN_BUTTONS, order = "createDate desc")
public class HttpSample extends BaseEntity {

	protected static final String ACTION_COLUMN_BUTTONS = "<@btn view='view'/>"
			+ "<@btn view='input' label='edit' windowoptions='{\"minWidth\":\"750\"}'/> <@btn action='singleShot'/>"
			+ "<@btn view='benchmark' label='benchmark' windowoptions='{\"minHeight\":\"200\"}'/>"
			+ "<a href='<@url value='/testingRecord/displayChart?sampleId='/>${(entity.id)!}' target='_blank' class='btn'>chart</a>";

	private static final String BODY_INPUT_TEMPLATE = "<textarea id='httpSample-body' name='httpSample.body' "
			+ "class='input-xxlarge' style='min-height: 200px'>${(entity.body)!}</textarea>";

	private static final long serialVersionUID = 5801606517538547923L;

	@UiConfig(alias = "接口名称", width = "150px")
	private String name;

	@UiConfig(alias = "地址", width = "450px", cssClass = "input-xxlarge")
	private String url;

	@UiConfig(alias = "请求方法", width = "50px", cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private RequestMethod method;

	@UiConfig(alias = "请求头")
	@Convert(converter = HttpHeaderListConverter.class)
	private List<HttpHeader> headers;

	@Column(length = 2048)
	@UiConfig(alias = "请求体", hiddenInList = @Hidden(true), type = "textarea", inputTemplate = BODY_INPUT_TEMPLATE, excludedFromQuery = true)
	private String body;

	@Transient
	@UiConfig(alias = "文件", hiddenInList = @Hidden(true), hiddenInView = @Hidden(true))
	private File csvFile;

	@Transient
	@UiConfig(hidden = true)
	private String csvFileFileName;

	@UiConfig(alias = "文件路径", width = "150px", readonly = @Readonly(true), excludedFromQuery = true)
	private String csvFilePath;

	@UiConfig(alias = "变量名", description = "以','间隔", width = "150px", excludedFromQuery = true)
	private String variableNames;

	@JsonIgnore
	@CreationTimestamp
	@Column(updatable = false)
	@UiConfig(hiddenInList = @Hidden(true), hiddenInInput = @Hidden(true), excludedFromQuery = true)
	private Date createDate; // 创建时间

	@JsonIgnore
	@UpdateTimestamp
	@Column(insertable = false)
	@UiConfig(hiddenInList = @Hidden(true), hiddenInInput = @Hidden(true), excludedFromQuery = true)
	private Date modifyDate; // 修改时间
}
