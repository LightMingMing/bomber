package com.bomber.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.manager.HttpSampleManager;
import com.bomber.vo.SimpleHttpSample;

@RestController
@RequestMapping("/httpSamples")
public class HttpSampleController {

	private final HttpSampleManager httpSampleManager;

	public HttpSampleController(HttpSampleManager httpSampleManager) {
		this.httpSampleManager = httpSampleManager;
	}

	@GetMapping
	public List<SimpleHttpSample> list(String projectId) {
		return httpSampleManager.getSimpleHttpSampleList(projectId);
	}

	@PutMapping("/orderNumber")
	public void updateOrderNumber(@RequestBody List<String> idList) {
		httpSampleManager.updateOrderNumber(idList);
	}

}
