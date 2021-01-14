package com.bomber.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.service.ProjectService;

@RestController
@RequestMapping("/project")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping("/projectName")
	public String getProjectName(String id) {
		return projectService.getProjectName(id).orElse(null);
	}
}
