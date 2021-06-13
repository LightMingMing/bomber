package com.bomber.entity;

import java.io.Serializable;

/**
 * @author MingMing Zhao
 */
public interface Persistable<ID> extends Serializable {

	ID getId();

	boolean isNew();
}
