package com.bomber.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.ironrhino.core.util.AppInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.functions.core.DefaultFunctionExecutor;
import com.bomber.functions.core.FunctionContext;
import com.bomber.functions.core.FunctionExecutor;
import com.bomber.manager.PayloadManager;
import com.bomber.model.Payload;
import com.bomber.model.PayloadOption;

@RestController
@RequestMapping("/payload")
public class PayloadController {

	private final static int MAX_LIMIT = 100_000;

	private final PayloadManager payloadManager;

	public PayloadController(PayloadManager payloadManager) {
		this.payloadManager = payloadManager;
	}

	public static String getPayloadApiUrl(String id) {
		String format = "http://%s%s%s/api/payload/" + id;
		String address = AppInfo.getHostAddress();
		int port = AppInfo.getHttpPort();
		String contextPath = AppInfo.getContextPath();
		if (port > 0 && port != 80) {
			return String.format(format, address, ":" + port, contextPath);
		} else {
			return String.format(format, address, "", contextPath);
		}
	}

	@GetMapping("/{id}")
	public List<Map<String, String>> get(@PathVariable String id, @RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "100") int limit, @RequestParam(required = false) Set<String> columns) {
		if (limit > MAX_LIMIT) {
			throw new IllegalArgumentException(
					String.format("limit is %d, expect less than or equal to %d", limit, MAX_LIMIT));
		}
		Payload payload = payloadManager.get(id);
		if (payload == null) {
			throw new IllegalArgumentException("payload does not exist");
		}
		List<FunctionContext> all = payload.getOptions().stream().map(PayloadOption::map).collect(Collectors.toList());

		FunctionExecutor executor = new DefaultFunctionExecutor(all, columns);
		try {
			return executor.execute(offset, limit);
		} finally {
			executor.shutdown();
		}
	}
}
