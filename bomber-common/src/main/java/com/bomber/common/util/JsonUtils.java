package com.bomber.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json Utils
 *
 * @author MingMing Zhao
 */
public class JsonUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	public static ObjectMapper objectMapper() {
		return objectMapper;
	}

	public static <T> T fromJson(String json, Class<T> cls)
			throws JsonProcessingException {
		return objectMapper.readValue(json, cls);
	}

	public static String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
