package com.bomber.functions;

import java.util.Map;

public interface Function<T> {

	String getRequiredArgs();

	String getOptionalArgs();

	boolean outputAllInputArgs();

	String getOutputArgNames();

	String getOutputArgValues();

	Class<T> returnType();

	void init(Map<String, String> params);

	void skip(int steps);

	T execute();

}
