package com.bomber.model;

import static com.bomber.functions.FunctionHelper.getFunctionMetadata;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.UiConfig;

import com.bomber.functions.FunctionMetadata;
import com.bomber.functions.FunctionOption;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Embeddable
public class PayloadOption {

	@Getter
	@Setter
	@Column(nullable = false)
	@UiConfig(description = "placeholder property")
	private String key;

	@Getter
	@Setter
	@Column(nullable = false)
	@UiConfig(alias = "functionName", type = "select", listKey = "key", listValue = "value", listOptions = "statics['com.bomber.functions.FunctionHelper'].getLabelValues()", cssClass = "function-type")
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
	private String argumentValues;

	@Getter
	@Setter
	@UiConfig(alias = "remark")
	private String remark;

	public String getRequiredArgs() {
		FunctionMetadata fm = getFunctionMetadata(functionName);
		return fm == null ? null : fm.getRequiredArgs();
	}

	public String getOptionalArgs() {
		FunctionMetadata fm = getFunctionMetadata(functionName);
		return fm == null ? null : fm.getOptionalArgs();
	}

	public FunctionOption map() {
		FunctionOption option = new FunctionOption();
		option.setKey(key);
		option.setFunctionName(functionName);
		option.setArgumentValues(argumentValues);
		return option;
	}
}
