package com.bomber.entity;

/**
 * @author MingMing Zhao
 */
public abstract class BaseEntity<ID> implements Persistable<ID> {

	private static final long serialVersionUID = 368137669337955973L;

	protected ID id;

	@Override
	public ID getId() {
		return id;
	}

	protected void setId(ID id) {
		this.id = id;
	}

	@Override
	public boolean isNew() {
		return id == null;
	}
}
