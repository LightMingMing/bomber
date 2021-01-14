package com.bomber.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.manager.ProjectManager;
import com.bomber.service.ProjectService;

import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

	private final ProjectManager projectManager;

	public ProjectServiceImpl(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<String> getProjectName(String id) {
		return projectManager.getProjectName(id);
	}
}
