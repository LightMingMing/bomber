package com.bomber.functions;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;
import org.ironrhino.core.util.DateUtils;

@FuncInfo(requiredArgs = "date", optionalArgs = "format")
public class Date2Timestamp extends StringFunction {

	private static final String DEFAULT_FORMAT = DateUtils.DATETIME;

	@Override
	public String execute(Input input) {
		String date = input.get("date");
		String format = input.getOrDefault("format", DEFAULT_FORMAT);
		return DateUtils.parse(date, format).getTime() + "";
	}
}
