package com.bomber.function.model;

import java.util.Objects;

import com.bomber.function.Function;
import lombok.Getter;
import lombok.Setter;

/**
 * 函数元信息
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class FunctionMetadata {

	private String name;

	private Class<Function> functionType;

	private String requiredArgs;

	private String optionalArgs;

	private String customArg;

	private boolean retAllArgs = false;

	private String retArg;

	private boolean parallel;

	private FunctionConstructors<Function> constructors;

	private MethodInvoker methodInvoker;

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
