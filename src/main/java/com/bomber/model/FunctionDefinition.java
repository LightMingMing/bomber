package com.bomber.model;

import static com.bomber.functions.util.FunctionHelper.getFunctionMetadata;

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

import com.bomber.functions.core.DefaultFunctionContext;
import com.bomber.functions.core.FunctionContext;
import com.bomber.functions.core.FunctionMetadata;
import com.bomber.functions.core.Input;
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
	@UiConfig(alias = "functionName", type = "select", listKey = "key", listValue = "value", listOptions = "statics['com.bomber.functions.util.FunctionHelper'].getLabelValues()", cssClass = "function-type")
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
		Input input = Input.EMPTY;
		if (argumentValues != null && !argumentValues.isEmpty()) {
			Map<String, String> params = new HashMap<>();
			for (String each : argumentValues) {
				String[] pair = each.split("=", 2);
				params.put(pair[0], pair[1]);
			}
			input = new Input(params);
		}
		return new DefaultFunctionContext(key, functionName, input);
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
