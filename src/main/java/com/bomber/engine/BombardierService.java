package com.bomber.engine;

import org.ironrhino.rest.client.RestApi;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bomber.engine.rpc.BombardierRequest;
import com.bomber.engine.rpc.BombardierResponse;

@RequestMapping("/pt")
@RestApi(apiBaseUrl = "${bombardier.apiBaseUrl:http://localhost:8081/api}", restTemplate = "infiniteTimeoutRestTemplate")
public interface BombardierService {

	@PostMapping(headers = "Content-Type=application/json")
	BombardierResponse execute(@RequestBody BombardierRequest bombardierRequest);

}
