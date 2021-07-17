package com.bomber.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author MingMing Zhao
 */
public interface Persistable<ID> extends Serializable {

	ID getId();

	@JsonIgnore
	boolean isNew();
}
