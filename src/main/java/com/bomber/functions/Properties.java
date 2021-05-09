package com.bomber.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.bomber.common.util.StringReplacer;
import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.MapFunction;

@FuncInfo(retAllArgs = true)
public class Properties extends MapFunction {

	private Map<String, String> properties;

	private boolean includeVariable;

	@Override
	public void init(Input input) {
		this.properties = Collections.unmodifiableMap(input.getAll());
		this.includeVariable = properties.values().stream().anyMatch(StringReplacer::supports);
	}

	@Override
	public Map<String, String> execute(Input input) {
		if (includeVariable) {
			Map<String, String> result = new HashMap<>(properties.size());
			properties.forEach((k, v) -> result.put(k, input.getOrDefault(k, v)));
			return result;
		}
		return properties;
	}

}
