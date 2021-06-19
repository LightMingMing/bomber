package com.bomber.handler;

import java.util.List;

import com.bomber.common.util.JsonUtils;
import com.bomber.entity.HttpHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author MingMing Zhao
 */
public class HttpHeaderListHandler extends JsonTypeHandler<List<HttpHeader>> {

	protected List<HttpHeader> readValue(String json) throws JsonProcessingException {
		return JsonUtils.fromJson(json, new TypeReference<>() {
		});
	}

}
