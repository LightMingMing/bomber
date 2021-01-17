package com.bomber.manager.impl;

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.manager.ProjectManager;
import com.bomber.model.Project;
import com.bomber.model.Project_;

@Repository
public class ProjectManagerImpl extends BaseManagerImpl<Project> implements ProjectManager {

	@Override
	@Transactional(readOnly = true)
	public Optional<String> getProjectName(String id) {
		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();

		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Project> root = cq.from(Project.class);

		cq.select(root.get(Project_.NAME));
		cq.where(cb.equal(root.get(Project_.ID), id));

		return sessionFactory.getCurrentSession().createQuery(cq).uniqueResultOptional();
	}
}
