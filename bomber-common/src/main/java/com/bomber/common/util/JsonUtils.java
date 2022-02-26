package com.bomber.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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

	public static <T> T fromJson(String json, TypeReference<T> typeReference)
		throws JsonProcessingException {
		return objectMapper.readValue(json, typeReference);
	}

	public static String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	public static String unprettify(String json) {
		try {
			JsonNode node = objectMapper.readTree(json);
			return objectMapper.writeValueAsString(node);
		} catch (Exception var2) {
			return json;
		}
	}

	public static String prettify(String json) {
		try {
			JsonNode node = objectMapper.readTree(json);
			ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
			return writer.writeValueAsString(node);
		} catch (Exception var3) {
			return json;
		}
	}
}
