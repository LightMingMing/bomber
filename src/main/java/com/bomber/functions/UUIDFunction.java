package com.bomber.functions;

import java.util.Map;
import java.util.UUID;

public class UUIDFunction extends AbstractStringFunction {

	private static final char hyphen = '-';

	private boolean noHyphen = true;

	@Override
	protected void doInit(Map<String, String> params) {
		this.noHyphen = Boolean.parseBoolean(params.getOrDefault("noHyphen", "true"));
	}

	@Override
	public String execute() {
		String uuid = UUID.randomUUID().toString();
		return noHyphen ? uuid.replace(hyphen, '\0') : uuid;
	}

	@Override
	public String getOptionalArgs() {
		return "noHyphen";
	}
}
