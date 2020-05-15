package com.bomber.model;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "http_sample")
@Richtable(showQueryForm = true, actionColumnButtons = HttpSample.ACTION_COLUMN_BUTTONS, order = "createDate desc")
public class HttpSample extends BaseEntity {

	final static String ACTION_COLUMN_BUTTONS = "<@btn view='view'/><@btn view='input' label='edit'/>"
			+ "<@btn action='singleShot'/><@btn view='benchmark' label='benchmark' windowoptions='{\"minHeight\":\"200\"}'/>"
			+ "<a href='<@url value='/testingRecord/displayChart?sampleId='/>${(entity.id)!}' target='_blank' class='btn'>chart</a>";

	final static String HEADER_INPUT_TEMPLATE = "<textarea id='httpSample-headers' name='httpSample.headers' "
			+ "class='input-xxlarge' style='height: 50px'>${(entity.headers)!}</textarea>";

	final static String BODY_INPUT_TEMPLATE = "<textarea id='httpSample-body' name='httpSample.body' "
			+ "class='input-xxlarge' style='min-height: 200px'>${(entity.body)!}</textarea>";

	@UiConfig(alias = "接口名称", width = "150px")
	private String name;

	@UiConfig(alias = "地址", width = "300px", cssClass = "input-xxlarge")
	private String url;

	@UiConfig(alias = "请求方法", width = "50px")
	private RequestMethod method;

	@UiConfig(alias = "请求头", type = "textarea", width = "150px", inputTemplate = HEADER_INPUT_TEMPLATE, excludedFromQuery = true)
	private List<String> headers;

	@Column(length = 2048)
	@UiConfig(alias = "请求体", maxlength = 2048, hiddenInList = @Hidden(true), type = "textarea", inputTemplate = BODY_INPUT_TEMPLATE, excludedFromQuery = true)
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
	@Column(insertable = false)
	@UiConfig(hiddenInList = @Hidden(true), hiddenInInput = @Hidden(true), excludedFromQuery = true)
	protected Date modifyDate; // 修改时间
}
