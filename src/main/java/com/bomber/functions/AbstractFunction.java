package com.bomber.functions;

import java.util.Map;

import org.springframework.lang.Nullable;

public abstract class AbstractFunction<T> implements Function<T> {

	// TODO
	// protected Class<T> returnType = (Class<T>)
	// ReflectionUtils.getGenericClass(this.getClass());

	@Override
	public String getRequiredArgs() {
		return null;
	}

	@Override
	public String getOptionalArgs() {
		return null;
	}

	@Override
	public boolean outputAllInputArgs() {
		return false;
	}

	@Override
	public String getOutputArgNames() {
		return null;
	}

	@Override
	public String getOutputArgValues() {
		return null;
	}

	@Override
	public void init(@Nullable Map<String, String> params) {
		String required = getRequiredArgs();
		if (required != null) {
			if (params == null || params.isEmpty()) {
				throw new IllegalArgumentException("Missing required args");
			}
			String[] args = required.split(", *");
			for (String arg : args) {
				if (!params.containsKey(arg)) {
					throw new IllegalArgumentException("Missing required arg: '" + arg + "'");
				}
			}
		}
		if (params != null) {
			doInit(params);
		}
	}

	@Override
	public void skip(int steps) {
	}

	protected abstract void doInit(Map<String, String> params);

}
