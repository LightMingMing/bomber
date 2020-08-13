package com.bomber.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.bomber.functions.Function;
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

		List<PayloadOption> options = payload.getOptions();
		Map<String, Function> functionMap = new HashMap<>();

		if (columns == null) {
			for (PayloadOption option : options) {
				Function func = option.createQuietly();
				func.skip(offset);
				functionMap.put(option.getKey(), func);
			}
		} else {
			Set<String> allColumns = options.stream().map(PayloadOption::getKey).collect(Collectors.toSet());
			for (String column : columns) {
				if (!allColumns.contains(column)) {
					throw new IllegalArgumentException("invalid column '" + column + "'");
				}
			}
			for (PayloadOption option : options) {
				if (columns.contains(option.getKey())) {
					Function func = option.createQuietly();
					func.skip(offset);
					functionMap.put(option.getKey(), func);
				}
			}
		}

		List<Map<String, String>> data = new ArrayList<>(limit);
		for (int i = 0; i < limit; i++) {
			Map<String, String> map = new HashMap<>(functionMap.size());
			functionMap.forEach((name, func) -> map.put(name, func.execute()));
			data.add(map);
		}
		return data;
	}
}
