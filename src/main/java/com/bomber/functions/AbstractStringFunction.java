package com.bomber.functions;

public abstract class AbstractStringFunction extends AbstractFunction<String> {
	@Override
	public Class<String> returnType() {
		return String.class;
	}
}
