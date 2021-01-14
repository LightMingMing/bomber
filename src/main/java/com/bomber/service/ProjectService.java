package com.bomber.service;

import java.util.Optional;

public interface ProjectService {

	Optional<String> getProjectName(String id);
}
