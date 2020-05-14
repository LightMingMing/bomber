package com.bomber.service;

import org.ironrhino.rest.client.RestApi;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/pt")
@RestApi(apiBaseUrl = "http://localhost:8081/api", restTemplate = "longTimeoutRestTemplate")
public interface BombardierService {

	@PostMapping(headers = "Content-Type=application/json")
	TestingResult execute(@RequestBody TestingPlan testingPlan);
}
