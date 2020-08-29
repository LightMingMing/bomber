package com.bomber.functions;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(optionalArgs = "noHyphen")
public class UUID extends StringFunction {

	private static final char hyphen = '-';

	private boolean noHyphen = true;

	@Override
	public void init(Input input) {
		this.noHyphen = Boolean.parseBoolean(input.getOrDefault("noHyphen", "true"));
	}

	@Override
	public String execute(Input input) {
		String uuid = java.util.UUID.randomUUID().toString();
		return noHyphen ? uuid.replace(hyphen, '\0') : uuid;
	}

}
