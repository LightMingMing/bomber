package com.bomber.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.entity.Persistable;

/**
 * @author MingMing Zhao
 */
public interface PagingMapper<ID, E extends Persistable<ID>> extends BaseMapper<ID, E> {

	long count();

	List<E> findAll(Pageable pageable);

	@Transactional
	default Page<E> paging(Pageable pageable) {
		long total = count();
		List<E> content = findAll(pageable);
		return new PageImpl<>(content, pageable, total);
	}
}
