package com.bomber.function;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bomber.entity.ArgumentValue;
import com.bomber.entity.FunctionConfigure;
import com.bomber.function.model.DefaultFunctionContext;
import com.bomber.function.model.FunctionContext;
import com.bomber.function.runner.DefaultFunctionExecutor;
import com.bomber.mapper.FunctionConfigureMapper;

/**
 * @author MingMing Zhao
 */
@Service
public class FunctionGeneratorImpl implements FunctionGenerator {

	private final FunctionConfigureMapper functionConfigureMapper;

	public FunctionGeneratorImpl(FunctionConfigureMapper functionConfigureMapper) {
		this.functionConfigureMapper = functionConfigureMapper;
	}

	private List<FunctionContext> createContextList(Integer id) {
		return functionConfigureMapper.findAllByWorkspace(id)
			.stream().map(this::createContext).collect(Collectors.toList());
	}

	private FunctionContext createContext(FunctionConfigure config) {
		return new DefaultFunctionContext(config.getName(), config.getFunctionName(), config.getArgumentValues()
			.stream().collect(Collectors.toMap(ArgumentValue::getName, ArgumentValue::getValue, (v1, v2) -> v2)));
	}

	@Override
	public Map<String, String> generateOne(Integer id, int offset) {
		return new DefaultFunctionExecutor(createContextList(id)).execute(offset);
	}

	@Override
	public List<Map<String, String>> generateBatch(Integer id, int offset, int size, Collection<String> selectedColumns) {
		return new DefaultFunctionExecutor(createContextList(id), selectedColumns).executeBatch(offset, size);
	}
}
