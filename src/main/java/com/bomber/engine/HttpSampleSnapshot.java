package com.bomber.engine;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpSampleSnapshot {

	private HttpMethod method;

	private String url;

	private String[] headers;

	private String body;

	private String variableNames;

	private String variableFilePath;

}
