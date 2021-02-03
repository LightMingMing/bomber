package com.bomber.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.manager.ProjectManager;

@RestController
@RequestMapping("/projects")
public class ProjectController {

	private final ProjectManager projectManager;

	public ProjectController(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@GetMapping("/{id}/name")
	public String getProjectName(@PathVariable String id) {
		return projectManager.getProjectName(id).orElse(null);
	}
}
