package com.bomber.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ValueReplacer {

	public static boolean containsReplaceableKeys(String source) {
		if (source == null || source.isEmpty()) {
			return false;
		}
		char p = ' '; // previous
		boolean matched = false;

		for (char c : source.toCharArray()) {
			if (c == '{' && p == '$') {
				matched = true;
			} else if (c == '}' && matched) {
				return true;
			}
			p = c;
		}
		return false;
	}

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
		return replace(source, context, false);
	}

	public static String replaceIfPresent(String source, Map<String, String> context) {
		return replace(source, context, true);
	}

	public static String replace(String source, Map<String, String> context, boolean onlyPresent) {
		if (source == null || source.isEmpty() || context == null || context.isEmpty()) {
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
				String k = key.toString();
				String defaultValue = onlyPresent ? "${" + k + "}" : k;
				sb.append(context.getOrDefault(k, defaultValue));
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
}
