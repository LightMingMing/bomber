package com.bomber.functions;

import static com.bomber.util.ValueReplacer.readReplaceableKeys;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import lombok.Getter;

@Getter
public class FunctionOption {

	private final String key;
	private final String functionName;
	private final FunctionMetadata metadata;
	private final Map<String, String> params;

	private final Set<String> dependentKeys;
	private final Set<String> outputKeys;

	public FunctionOption(String key, String functionName, @NonNull Map<String, String> params) {
		this.key = key;
		this.functionName = functionName;
		this.params = params;
		this.metadata = FunctionHelper.getFunctionMetadata(functionName);
		if (this.metadata == null) {
			throw new IllegalArgumentException("function '" + functionName + "' can't found");
		}
		this.dependentKeys = readDependentKeys();
		this.outputKeys = readOutputKeys();
	}

	public Set<String> readDependentKeys() {
		if (params.isEmpty()) {
			return Collections.emptySet();
		}
		return readReplaceableKeys(params.values());
	}

	private Set<String> readOutputKeys() {
		if (metadata.isOutputAllInputArgs()) {
			return params.keySet();
		} else if (metadata.getOutputArgNames() != null) {
			String[] args = metadata.getOutputArgNames().split(", *");
			return Arrays.stream(args).filter(StringUtils::hasLength).collect(Collectors.toSet());
		} else if (metadata.getOutputArgValues() != null) {
			Set<String> output = new HashSet<>();
			String[] keys = metadata.getOutputArgValues().split(", *");
			for (String key : keys) {
				if (params.containsKey(key)) {
					String retValues = params.get(key);
					output.addAll(Arrays.asList(retValues.split(", *")));
				}
			}
			return output;
		}
		return Collections.singleton(key);
	}

}
