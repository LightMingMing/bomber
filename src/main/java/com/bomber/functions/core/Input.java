package com.bomber.functions.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Input {

	public static Input EMPTY = new Input();

	protected final Map<String, String> params;

	public Input() {
		this((Map<String, String>) null);
	}

	public Input(String... params) {
		if (params.length == 0) {
			this.params = Collections.emptyMap();
			return;
		}
		if ((params.length & 1) == 1) {
			throw new IllegalArgumentException("The length of params should be a multiple of 2 ");
		}
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < params.length; i += 2) {
			map.put(params[i], params[i + 1]);
		}
		this.params = Collections.unmodifiableMap(map);
	}

	public Input(Map<String, String> params) {
		this.params = params == null ? Collections.emptyMap() : Collections.unmodifiableMap(params);
	}

	public String get(String arg) {
		return params.get(arg);
	}

	public String getOrDefault(String arg, String defaultValue) {
		return params.getOrDefault(arg, defaultValue);
	}

	public Set<String> keySet() {
		return params.keySet();
	}

	public Collection<String> values() {
		return params.values();
	}

	public boolean containsKey(String key) {
		return params.containsKey(key);
	}

	public Map<String, String> getAll() {
		return params;
	}

	public boolean isEmpty() {
		return params.isEmpty();
	}

	public int size() {
		return params.size();
	}
}
