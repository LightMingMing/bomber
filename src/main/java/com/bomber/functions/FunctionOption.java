package com.bomber.functions;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FunctionOption {

	private String key;

	private String functionName;

	private Map<String, String> params;
}
