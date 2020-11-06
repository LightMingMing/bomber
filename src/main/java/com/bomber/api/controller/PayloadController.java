package com.bomber.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bomber.service.PayloadGenerateService;
import org.ironrhino.core.util.AppInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payload")
public class PayloadController {

	private final PayloadGenerateService payloadGenerateService;

	public PayloadController(PayloadGenerateService payloadGenerateService) {
		this.payloadGenerateService = payloadGenerateService;
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
	public List<Map<String, String>> get(@PathVariable String id, @RequestParam int offset, @RequestParam int limit,
			@RequestParam(required = false) Set<String> columns) {
		return payloadGenerateService.generate(id, offset, limit, columns);
	}
}
