package com.bomber.manager.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.query.Query;
import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.manager.HttpSampleManager;
import com.bomber.model.HttpSample;
import com.bomber.model.HttpSample_;
import com.bomber.model.Project_;
import com.bomber.vo.SimpleHttpSample;

@Repository
public class HttpSampleManagerImpl extends BaseManagerImpl<HttpSample> implements HttpSampleManager {

	@Override
	@Transactional(readOnly = true)
	public List<SimpleHttpSample> getSimpleHttpSampleList(String projectId) {
		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();

		CriteriaQuery<SimpleHttpSample> cq = cb.createQuery(SimpleHttpSample.class);
		Root<HttpSample> root = cq.from(HttpSample.class);

		cq.multiselect(root.get(HttpSample_.ID), root.get(HttpSample_.NAME), root.get(HttpSample_.METHOD));
		cq.where(cb.equal(root.get(HttpSample_.PROJECT).get(Project_.ID), projectId));
		cq.orderBy(cb.asc(root.get(HttpSample_.ORDER_NUMBER)));
		return sessionFactory.getCurrentSession().createQuery(cq).getResultList();
	}

	@Override
	@Transactional
	@SuppressWarnings("rawtypes")
	public void updateOrderNumber(List<String> idList) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("update HttpSample s set s.orderNumber = :orderNumber where s.id = :id");

		for (int i = 0; i < idList.size(); i++) {
			query.setParameter("orderNumber", i);
			query.setParameter("id", idList.get(i));
			query.executeUpdate();
		}
	}
}
