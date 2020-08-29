package com.bomber.functions;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;
import org.ironrhino.core.util.CodecUtils;

@FuncInfo(requiredArgs = "length")
public class RandomString extends StringFunction {

	private int length;

	@Override
	public void init(Input input) {
		this.length = Integer.parseInt(input.get("length"));
	}

	@Override
	public String execute(Input input) {
		return CodecUtils.nextId(length);
	}

}