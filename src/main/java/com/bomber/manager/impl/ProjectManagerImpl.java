package com.bomber.manager.impl;

import com.bomber.manager.ProjectManager;
import com.bomber.model.Project;
import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class ProjectManagerImpl extends BaseManagerImpl<Project> implements ProjectManager {

	@Override
	@Transactional(readOnly = true)
	public Optional<String> getProjectName(String id) {
		CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();

		CriteriaQuery<String> query = criteriaBuilder.createQuery(String.class);
		Root<Project> root = query.from(Project.class);

		query.select(root.get("name"));
		query.where(criteriaBuilder.equal(root.get("id"), id));

		return sessionFactory.getCurrentSession().createQuery(query).uniqueResultOptional();
	}
}
