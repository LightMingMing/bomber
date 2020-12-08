package com.bomber.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bomber.functions.core.DefaultFunctionExecutor;
import com.bomber.functions.core.FunctionContext;
import com.bomber.functions.core.FunctionExecutor;
import com.bomber.model.Payload;
import com.bomber.model.FunctionDefinition;
import org.springframework.stereotype.Service;

import com.bomber.manager.PayloadManager;
import com.bomber.service.PayloadGenerateService;
import org.springframework.util.Assert;

@Service
public class PayloadGenerateServiceImpl implements PayloadGenerateService {

	private final PayloadManager payloadManager;

	public PayloadGenerateServiceImpl(PayloadManager payloadManager) {
		this.payloadManager = payloadManager;
	}

	private FunctionExecutor createExecutor(String id, Collection<String> columns) {
		Payload payload = payloadManager.get(id);
		Assert.notNull(payload, "payload '" + id + "' does not exist");
		List<FunctionContext> all = payload.getFunctionDefinitions().stream().map(FunctionDefinition::map)
				.collect(Collectors.toList());
		return new DefaultFunctionExecutor(all, columns);
	}

	@Override
	public Map<String, String> generate(String id, int start, Collection<String> columns) {
		FunctionExecutor executor = createExecutor(id, columns);
		try {
			executor.jump(start);
			return executor.execute();
		} finally {
			executor.shutdown();
		}
	}

	@Override
	public List<Map<String, String>> generate(String id, int start, int count, Collection<String> columns) {
		FunctionExecutor executor = createExecutor(id, columns);
		try {
			executor.jump(start);
			return executor.execute(count);
		} finally {
			executor.shutdown();
		}
	}
}
