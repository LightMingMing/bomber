package com.bomber.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.bomber.function.model.FunctionContext;
import com.bomber.function.runner.DefaultFunctionExecutor;
import com.bomber.function.runner.FunctionExecutor;
import com.bomber.manager.FunctionConfigureManager;
import com.bomber.model.FunctionConfigure;
import com.bomber.model.FunctionDefinition;
import com.bomber.service.PayloadGenerateService;

@Service
public class PayloadGenerateServiceImpl implements PayloadGenerateService {

	private final FunctionConfigureManager functionConfigureManager;

	public PayloadGenerateServiceImpl(FunctionConfigureManager functionConfigureManager) {
		this.functionConfigureManager = functionConfigureManager;
	}

	private FunctionExecutor createExecutor(String id, Collection<String> columns) {
		FunctionConfigure functionConfigure = functionConfigureManager.get(id);
		Assert.notNull(functionConfigure, "payload '" + id + "' does not exist");
		List<FunctionContext> all = functionConfigure.getFunctionDefinitions().stream().map(FunctionDefinition::map)
				.collect(Collectors.toList());
		return new DefaultFunctionExecutor(all, columns);
	}

	@Override
	public Map<String, String> generate(String id, int start, Collection<String> columns) {
		return createExecutor(id, columns).execute(start);
	}

	@Override
	public List<Map<String, String>> generate(String id, int start, int count, Collection<String> columns) {
		return createExecutor(id, columns).executeBatch(start, count);
	}
}
