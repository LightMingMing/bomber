package com.bomber.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import com.bomber.converter.PayloadOptionListConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payload")
@Richtable(showQueryForm = true, celleditable = false, actionColumnButtons = "<@btn view='view'/> <@btn view='input' label='edit'/>  <@btn view='quickCreate' label='clone'/> <@btn view='preview' label='preview'/>", order = "createDate desc")
public class Payload extends BaseEntity {

	private static final long serialVersionUID = -6844125829687712537L;

	@Column(nullable = false)
	@UiConfig(width = "200px", cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private String name;

	@Column(nullable = false, columnDefinition = "text")
	@Convert(converter = PayloadOptionListConverter.class)
	@UiConfig(alias = "payloadOptions", listTemplate = "<#list value as option>${option.key}= ${option.functionName}(${option.argumentValues!})<#sep><br></#list>")
	private List<PayloadOption> options;

	@CreationTimestamp
	@Column(updatable = false)
	@UiConfig(width = "150px", hiddenInInput = @Hidden(true), excludedFromQuery = true)
	private Date createDate;

	@UpdateTimestamp
	@Column(insertable = false)
	@UiConfig(width = "150px", hiddenInInput = @Hidden(true), excludedFromQuery = true)
	private Date modifyDate;

	@Version
	private int version = -1;

	@Override
	public String toString() {
		return name;
	}
}
