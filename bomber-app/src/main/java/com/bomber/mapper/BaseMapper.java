package com.bomber.mapper;

import java.util.Optional;

import com.bomber.entity.Persistable;

/**
 * @author MingMing Zhao
 */
public interface BaseMapper<ID, E extends Persistable<ID>> {

	int create(E e);

	int delete(ID id);

	int update(E e);

	Optional<E> select(ID id);

	default int save(E e) {
		return e.isNew() ? create(e) : update(e);
	}

}
