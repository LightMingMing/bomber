package com.bomber.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@AllArgsConstructor
public class SimpleHttpSample {

	private String id;

	private String name;

	private HttpMethod httpMethod;
}