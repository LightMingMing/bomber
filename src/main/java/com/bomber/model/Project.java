package com.bomber.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.ironrhino.core.hibernate.CreationUser;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AutoConfig
@Table(name = "project")
@Richtable(showQueryForm = true, celleditable = false, order = "createDate desc")
public class Project extends BaseEntity {

	private static final long serialVersionUID = -6035022618157843454L;

	@Column(nullable = false)
	@UiConfig(listTemplate = "<a href='<@url value='/workbench?projectId='/>${(entity.id)!}'>${value}</a>")
	private String name;

	@UiConfig(excludedFromQuery = true, type = "textarea")
	private String description;

	@CreationUser
	@Column(updatable = false)
	@UiConfig(hiddenInInput = @Hidden(true))
	private String author;

	@JsonIgnore
	@CreationTimestamp
	@Column(updatable = false)
	@UiConfig(hiddenInInput = @Hidden(true))
	private Date createDate;

	@JsonIgnore
	@UpdateTimestamp
	@Column(insertable = false)
	@UiConfig(hiddenInList = @Hidden(true), hiddenInInput = @Hidden(true), excludedFromQuery = true)
	private Date modifyDate;
}
