package com.bomber.http.client;

import org.ironrhino.core.spring.http.client.RestTemplate;
import org.springframework.stereotype.Component;

@Component
public class InfiniteTimeoutRestTemplate extends RestTemplate {

	@Override
	public void setReadTimeout(int readTimeout) {
		super.setReadTimeout(0);
	}

}
