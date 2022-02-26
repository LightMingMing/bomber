
package com.bomber.http.client;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class StringMessageRestTemplate extends RestTemplate {

	public StringMessageRestTemplate() {
		super();
		setMessageConverters(Collections.singletonList(new StringHttpMessageConverter(StandardCharsets.UTF_8)));
	}

}