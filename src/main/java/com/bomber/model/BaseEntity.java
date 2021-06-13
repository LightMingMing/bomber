package com.bomber.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.ironrhino.core.hibernate.type.JsonType;
import org.ironrhino.core.model.AbstractEntity;
import org.ironrhino.core.search.elasticsearch.annotations.SearchableId;

/**
 * 自增主键
 *
 * @author MingMing Zhao
 */
@MappedSuperclass
@TypeDefs({@TypeDef(name = "json", typeClass = JsonType.class)})
public abstract class BaseEntity extends AbstractEntity<Long> {

	private static final long serialVersionUID = 7392373718392885241L;

	@Id
	@SearchableId
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

