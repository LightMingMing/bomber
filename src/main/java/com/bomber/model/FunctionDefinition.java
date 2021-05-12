package com.bomber.model;

import static com.bomber.function.util.FunctionHelper.getFunctionMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.UiConfig;

import com.bomber.function.model.DefaultFunctionContext;
import com.bomber.function.model.FunctionContext;
import com.bomber.function.model.FunctionMetadata;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Embeddable
public class FunctionDefinition {

	@Getter
	@Setter
	@Column(nullable = false)
	@UiConfig(description = "placeholder property")
	private String key;

	@Getter
	@Setter
	@Column(nullable = false)
	@UiConfig(alias = "functionName", type = "select", listKey = "key", listValue = "value", listOptions = "statics['com.bomber.function.util.FunctionHelper'].getLabelValues()", cssClass = "function-type")
	private String functionName;

	@Setter
	@Transient
	@JsonIgnore
	@UiConfig(alias = "requiredArgs", readonly = @Readonly(true), cssClass = "required-args")
	private String requiredArgs;

	@Setter
	@Transient
	@JsonIgnore
	@UiConfig(alias = "optionalArgs", readonly = @Readonly(true), cssClass = "optional-args")
	private String optionalArgs;

	@Getter
	@Setter
	@UiConfig(alias = "argumentValues", cssClass = "input-xxlarge")
	private List<String> argumentValues;

	@Getter
	@Setter
	@Transient
	@JsonIgnore
	@UiConfig(hiddenInView = @Hidden(true))
	private String content;

	public String getRequiredArgs() {
		FunctionMetadata fm = getFunctionMetadata(functionName);
		return fm == null ? null : fm.getRequiredArgs();
	}

	public String getOptionalArgs() {
		FunctionMetadata fm = getFunctionMetadata(functionName);
		return fm == null ? null : fm.getOptionalArgs();
	}

	public FunctionContext map() {
		if (argumentValues != null && !argumentValues.isEmpty()) {
			Map<String, String> params = new HashMap<>();
			for (String each : argumentValues) {
				String[] pair = each.split("=", 2);
				params.put(pair[0], pair[1]);
			}
			return new DefaultFunctionContext(key, functionName, params);
		}
		return new DefaultFunctionContext(key, functionName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FunctionDefinition that = (FunctionDefinition) o;
		return key.equals(that.key) && functionName.equals(that.functionName)
				&& Objects.equals(argumentValues, that.argumentValues);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, functionName, argumentValues);
	}
}
