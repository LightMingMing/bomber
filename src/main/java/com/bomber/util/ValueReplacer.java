package com.bomber.util;

import java.util.Map;

public class ValueReplacer {

	public static String replace(String source, Map<String, String> variables) {
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
				sb.append(getValue(key.toString(), variables));
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

	private static String getValue(String key, Map<String, String> variables) {
		return variables.getOrDefault(key, key);
	}
}
