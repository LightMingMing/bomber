package com.bomber.http.client;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.ironrhino.core.spring.http.client.RestTemplate;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class StringMessageRestTemplate extends RestTemplate {

	public StringMessageRestTemplate() {
		super();
		setMessageConverters(Collections.singletonList(new StringHttpMessageConverter(StandardCharsets.UTF_8)));
	}
}