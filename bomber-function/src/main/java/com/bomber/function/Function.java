package com.bomber.function;

import java.util.Map;

import org.springframework.lang.NonNull;

import com.bomber.common.util.StringReplacer;

/**
 * 函数接口
 *
 * @author MingMing Zhao
 */
public interface Function extends Jumpable {

	default Object[] getParameterValues(@NonNull Map<String, String> initParameterValues, @NonNull Map<String, String> container) {
		return new Object[0];
	}

	default Object[] replace(Map<String, String> initParameterValues, Map<String, String> container, String... keys) {
		Object[] parameterValues = new Object[keys.length];
		for (int i = 0; i < keys.length; i++) {
			parameterValues[i] = StringReplacer.replace(initParameterValues.get(keys[i]), container);
		}
		return parameterValues;
	}
}
