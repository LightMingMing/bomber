package com.bomber.api.controller;

import com.bomber.service.HttpSampleResult;
import com.bomber.service.HttpSampleService;
import org.ironrhino.rest.RestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/httpSample")
public class HttpSampleController {

	private final HttpSampleService httpSampleService;

	public HttpSampleController(HttpSampleService httpSampleService) {
		this.httpSampleService = httpSampleService;
	}

	@GetMapping("/preview")
	public RestResult<String> preview(String id, int index) {
		return RestResult.of(httpSampleService.renderRequest(id, index));
	}

	@GetMapping(value = "/execute")
	public HttpSampleResult execute(String id) {
		return httpSampleService.execute(id);
	}

	@GetMapping(value = "/execute", params = "index")
	public HttpSampleResult execute(String id, int index) {
		return httpSampleService.execute(id, index);
	}

	@GetMapping(value = "/execute", params = {"index", "size"})
	public List<HttpSampleResult> execute(String id, int index, int size) {
		return httpSampleService.executeBatch(id, index, size);
	}
}
