package com.bomber.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface PayloadGenerateService {

	default Map<String, String> generate(String id) {
		return generate(id, 0);
	}

	default Map<String, String> generate(String id, int start) {
		return generate(id, start, Collections.emptyList());
	}

	default List<Map<String, String>> generate(String id, int start, int count) {
		return generate(id, start, count, Collections.emptyList());
	}

	default Map<String, String> generate(String id, Collection<String> columns) {
		return generate(id, 0, columns);
	}

	Map<String, String> generate(String id, int start, Collection<String> columns);

	List<Map<String, String>> generate(String id, int start, int count, Collection<String> columns);

}
