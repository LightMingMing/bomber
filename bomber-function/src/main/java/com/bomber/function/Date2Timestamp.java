package com.bomber.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.lang.NonNull;

/**
 * 日期转时间戳
 *
 * @author MingMing Zhao
 */
@Group(Type.DATE)
@FuncInfo(requiredArgs = "date", optionalArgs = "format")
public class Date2Timestamp implements Function {

	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private final SimpleDateFormat format;

	public Date2Timestamp() {
		this(DEFAULT_FORMAT);
	}

	public Date2Timestamp(String format) {
		this.format = new SimpleDateFormat(format);
	}

	public String execute(String date) throws ParseException {
		return String.valueOf(format.parse(date).getTime());
	}

	@Override
	public Object[] getParameterValues(@NonNull Map<String, String> initParameterValues, @NonNull Map<String, String> container) {
		return replace(initParameterValues, container, "date");
	}
}
