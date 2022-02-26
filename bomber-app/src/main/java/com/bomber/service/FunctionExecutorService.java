package com.bomber.service;

import com.bomber.entity.ArgumentValue;
import com.bomber.entity.FunctionConfigure;
import com.bomber.function.model.DefaultFunctionContext;
import com.bomber.function.model.FunctionContext;
import com.bomber.function.runner.DefaultFunctionExecutor;
import com.bomber.mapper.FunctionConfigureMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 函数生成
 *
 * @author MingMing Zhao
 */
@Service
public class FunctionExecutorService {

	private final FunctionConfigureMapper functionConfigureMapper;

	public FunctionExecutorService(FunctionConfigureMapper functionConfigureMapper) {
		this.functionConfigureMapper = functionConfigureMapper;
	}

	private List<FunctionContext> createContextList(Integer id) {
		return functionConfigureMapper.findAllByGroup(id)
			.stream().map(this::createContext).collect(Collectors.toList());
	}

	private FunctionContext createContext(FunctionConfigure config) {
		return new DefaultFunctionContext(config.getName(), config.getFunctionName(), config.getArgumentValues()
			.stream().collect(Collectors.toMap(ArgumentValue::getName, ArgumentValue::getValue, (v1, v2) -> v2)));
	}

	public Map<String, String> execute(Integer id, int offset) {
		return new DefaultFunctionExecutor(createContextList(id)).execute(offset);
	}

	public List<Map<String, String>> execute(Integer id, int offset, int size) {
		return new DefaultFunctionExecutor(createContextList(id)).executeBatch(offset, size);
	}

	public List<Map<String, String>> execute(Integer id, int offset, int size, Collection<String> selectedColumns) {
		return new DefaultFunctionExecutor(createContextList(id), selectedColumns).executeBatch(offset, size);
	}
}
