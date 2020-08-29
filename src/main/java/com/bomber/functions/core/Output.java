package com.bomber.functions.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Output {

	protected final Map<String, String> params;

	public Output() {
		this.params = new HashMap<>();
	}

	void put(String key, String value) {
		this.params.put(key, value);
	}

	void putAll(Map<String, String> m) {
		this.params.putAll(m);
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
