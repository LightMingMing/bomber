package com.bomber.engine.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 函数信息
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class FunctionInfo {

	private String key;

	private String name;

	private Map<String, String> initParameterValues;
}
