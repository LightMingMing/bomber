package com.bomber.functions;

import java.util.Map;

public interface Function {

	String getRequiredArgs();

	String getOptionalArgs();

	void init(Map<String, String> params);

	void skip(int steps);

	String execute();

}
