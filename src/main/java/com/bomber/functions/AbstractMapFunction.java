package com.bomber.functions;

import java.util.Map;

public abstract class AbstractMapFunction extends AbstractFunction<Map<String, String>> {

	@Override
	@SuppressWarnings("unchecked")
	public Class<Map<String, String>> returnType() {
		try {
			return (Class<Map<String, String>>) Class.forName("java.util.Map");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
