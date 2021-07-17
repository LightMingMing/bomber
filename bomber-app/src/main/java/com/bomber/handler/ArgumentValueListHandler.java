package com.bomber.handler;

import java.util.List;

import com.bomber.common.util.JsonUtils;
import com.bomber.entity.ArgumentValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * List&lt;ArgumentValue&gt; 类型处理器
 *
 * @author MingMing Zhao
 */
public class ArgumentValueListHandler extends JsonTypeHandler<List<ArgumentValue>> {
	@Override
	protected List<ArgumentValue> readValue(String json) throws JsonProcessingException {
		return JsonUtils.fromJson(json, new TypeReference<>() {
		});
	}
}
