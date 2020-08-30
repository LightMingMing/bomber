package com.bomber.functions;

import java.util.Collections;
import java.util.Map;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.MapFunction;

@FuncInfo(retAllArgs = true)
public class Properties extends MapFunction {

	private Map<String, String> properties;

	@Override
	public void init(Input input) {
		this.properties = Collections.unmodifiableMap(input.getAll());
	}

	@Override
	public Map<String, String> execute(Input input) {
		return properties;
	}

}
