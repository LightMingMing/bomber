package com.bomber.functions;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionMetadata {

	private String name;

	private Class<? extends Function<?>> functionType;

	private String requiredArgs;

	private String optionalArgs;

	private boolean outputAllInputArgs;

	private String outputArgNames;

	private String outputArgValues;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FunctionMetadata that = (FunctionMetadata) o;
		return Objects.equals(functionType, that.functionType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(functionType);
	}
}
