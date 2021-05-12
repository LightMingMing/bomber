package com.bomber.function;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.lang.NonNull;

/**
 * 时间戳转日期
 *
 * @author MingMing Zhao
 */
@FuncInfo(requiredArgs = "timestamp", optionalArgs = "format")
public class Timestamp2Date implements Function {

	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private final SimpleDateFormat format;

	public Timestamp2Date() {
		this(DEFAULT_FORMAT);
	}

	public Timestamp2Date(String format) {
		this.format = new SimpleDateFormat(format);
	}

	public String execute(String timestamp) {
		return format.format(new Date(Long.parseLong(timestamp)));
	}

	@Override
	public Object[] getParameterValues(@NonNull Map<String, String> initParameterValues, @NonNull Map<String, String> container) {
		return replace(initParameterValues, container, "timestamp");
	}
}
