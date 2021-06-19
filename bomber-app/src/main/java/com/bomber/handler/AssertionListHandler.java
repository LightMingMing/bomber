package com.bomber.handler;

import java.util.List;

import com.bomber.common.util.JsonUtils;
import com.bomber.entity.Assertion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author MingMing Zhao
 */
public class AssertionListHandler extends JsonTypeHandler<List<Assertion>> {
	@Override
	protected List<Assertion> readValue(String json) throws JsonProcessingException {
		return JsonUtils.fromJson(json, new TypeReference<>() {
		});
	}
}
