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

import com.bomber.converter.FunctionDefinitionListConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payload")
@Richtable(showQueryForm = true, celleditable = false, actionColumnButtons = "<@btn view='view'/> <@btn view='input' label='edit'/>  <@btn view='quickCreate' label='copy'/> <@btn view='preview' label='preview' windowoptions='{\"width\":\"50%\"}'/>", order = "createDate desc")
public class Payload extends BaseEntity {

	private static final long serialVersionUID = -6844125829687712537L;

	@Column(nullable = false)
	@UiConfig(width = "200px", cellDynamicAttributes = "{\"style\":\"text-align: center\"}")
	private String name;

	@Column(nullable = false, columnDefinition = "text")
	@Convert(converter = FunctionDefinitionListConverter.class)
	@UiConfig(alias = "functionDefinitions", listTemplate = "<#list value as functionDefinition>${functionDefinition.key}= ${functionDefinition.functionName}(${functionDefinition.argumentValues!})<#sep><br></#list>")
	private List<FunctionDefinition> functionDefinitions;

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
