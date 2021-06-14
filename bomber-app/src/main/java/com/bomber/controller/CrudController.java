package com.bomber.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.bomber.entity.Persistable;

/**
 * @author MingMing Zhao
 */
public interface CrudController<ID, E extends Persistable<ID>> {

	int create(E e);

	int delete(ID id);

	int update(E e);

	Optional<E> select(ID id);
}
