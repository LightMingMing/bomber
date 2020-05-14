package com.bomber.http.client;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
	@Bean
	public RestTemplate simpleRestTemplate() {
		return new RestTemplate((Collections.singletonList(new StringHttpMessageConverter())));
	}
}
