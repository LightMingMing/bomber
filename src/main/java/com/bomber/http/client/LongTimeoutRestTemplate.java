package com.bomber.http.client;

import org.ironrhino.core.spring.http.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LongTimeoutRestTemplate extends RestTemplate {

	private final int DEFAULT_LONG_TIMEOUT = 1000 * 60 * 20; // 20 min

	@Override
	@Value("${longTimeoutRestTemplate.readTimeout:" + DEFAULT_LONG_TIMEOUT + "}")
	public void setReadTimeout(int readTimeout) {
		super.setReadTimeout(Math.max(readTimeout, DEFAULT_LONG_TIMEOUT));
	}

}
