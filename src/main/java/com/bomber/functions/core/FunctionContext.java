package com.bomber.functions.core;

import java.util.Set;

public interface FunctionContext {

	String name();

	FunctionMetadata metadata();

	Function<?> function();

	Input input();

	Set<String> dependentKeys();

	Set<String> retKeys();

	void fireInit();

	void fireExecute(Output output);

	void fireJump(int steps);

	void fireClose();

}
