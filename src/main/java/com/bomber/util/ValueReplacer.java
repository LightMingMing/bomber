package com.bomber.util;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ValueReplacer {

	public static Set<String> getKeys(String source) {
		StringBuilder key = new StringBuilder();
		char[] charArray = source.toCharArray();
		char previous = ' ';

		boolean matched = false;

		Set<String> keys = new LinkedHashSet<>();
		for (char c : charArray) {
			if (c == '{' && previous == '$') {
				matched = true;
			} else if (c == '}' && matched) {
				keys.add(key.toString());
				key = new StringBuilder();
				matched = false;
			} else if (matched) {
				key.append(c);
			}
			previous = c;
		}
		return keys;
	}

	public static String replace(String source, Map<String, String> context) {
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
