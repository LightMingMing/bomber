package com.bomber.manager;

import com.bomber.model.Project;
import org.ironrhino.core.service.BaseManager;

import java.util.Optional;

public interface ProjectManager extends BaseManager<Project> {
	Optional<String> getProjectName(String id);
}
