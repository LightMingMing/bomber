package com.bomber.http.client;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.ironrhino.core.spring.http.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class StringMessageRestTemplate extends RestTemplate {

	private final int TIMEOUT = 1000 * 120; // 2 min

	public StringMessageRestTemplate() {
		super();
		setMessageConverters(Collections.singletonList(new StringHttpMessageConverter(StandardCharsets.UTF_8)));
	}

	@Override
	@Value("${stringMessageRestTemplate.readTimeout:" + TIMEOUT + "}")
	public void setReadTimeout(int readTimeout) {
		super.setReadTimeout(Math.max(readTimeout, TIMEOUT));
	}
}