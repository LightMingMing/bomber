package com.bomber.functions.core;

import java.util.Collection;
import java.util.List;

public interface DependencyHandler {

	List<FunctionContext> handle(Collection<FunctionContext> disordered);

	List<FunctionContext> handle(Collection<FunctionContext> disordered, String... chooses);

	List<FunctionContext> handle(Collection<FunctionContext> disordered, Collection<String> chooses);
}
