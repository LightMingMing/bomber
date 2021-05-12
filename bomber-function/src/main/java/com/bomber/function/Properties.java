package com.bomber.function;

import java.util.Collections;
import java.util.Map;

/**
 * 属性函数
 *
 * @author MingMing Zhao
 */
@FuncInfo(retAllArgs = true)
public class Properties implements Producer<Map<String, String>> {

	private final Map<String, String> properties;

	public Properties(Map<String, String> properties) {
		this.properties = Collections.unmodifiableMap(properties);
	}

	@Override
	public Map<String, String> execute() {
		return properties;
	}
}
