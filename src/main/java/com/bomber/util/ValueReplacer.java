package com.bomber.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ValueReplacer {

	public static Set<String> readReplaceableKeys(Collection<String> sources) {
		Set<String> result = new LinkedHashSet<>();
		for (String source : sources) {
			readReplaceableKeys(source, result);
		}
		return result;
	}

	public static Set<String> readReplaceableKeys(String source) {
		Set<String> result = new LinkedHashSet<>();
		readReplaceableKeys(source, result);
		return result;
	}

	private static void readReplaceableKeys(String source, Set<String> result) {
		char p = ' ';
		boolean matched = false;
		StringBuilder key = new StringBuilder();

		for (char c : source.toCharArray()) {
			if (c == '{' && p == '$') {
				matched = true;
			} else if (c == '}' && matched) {
				result.add(key.toString());
				key = new StringBuilder();
				matched = false;
			} else if (matched) {
				key.append(c);
			}
			p = c;
		}
	}

	public static String replace(String source, Map<String, String> context) {
		if (source == null || context == null) {
			return source;
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder key = new StringBuilder();
		char[] charArray = source.toCharArray();
		char previous = ' ';

		boolean matched = false;

		for (char c : charArray) {
			if (c == '{' && previous == '$') {
				matched = true;
				previous = c;
				sb.deleteCharAt(sb.length() - 1);
			} else if (c == '}' && matched) {
				sb.append(getValue(key.toString(), context));
				key = new StringBuilder();
				matched = false;
			} else if (matched) {
				key.append(c);
			} else {
				previous = c;
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private static String getValue(String key, Map<String, String> context) {
		return context.getOrDefault(key, key);
	}
}
