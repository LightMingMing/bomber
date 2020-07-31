package com.bomber.functions;

import org.ironrhino.core.util.CodecUtils;

import java.util.Map;

public class RandomStringFunction extends AbstractFunction {

	private int length;

	@Override
	public String execute() {
		return CodecUtils.nextId(length);
	}

	@Override
	public String getRequiredArgs() {
		return "length";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		this.length = Integer.parseInt(params.get("length"));
	}
}