package com.bomber.service;

import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BombardierRequest {
	@JsonProperty("numConns")
	private int numberOfConnections;

	@JsonProperty("numReqs")
	private int numberOfRequests;

	@JsonProperty("method")
	private RequestMethod method;

	private String headers;

	private String url;

	private String body;

	private String csvFilePath;

	private String variableNames;

	private int startLine;

	public BombardierRequest() {
	}

	public BombardierRequest(String url, RequestMethod requestMethod, String headers, String body) {
		this(url, requestMethod, headers, body, null, null);
	}

	public BombardierRequest(String url, RequestMethod requestMethod, String headers, String body, String csvFilePath,
			String variableNames) {
		this.url = url;
		this.method = requestMethod;
		this.headers = headers;
		this.body = body;
		if (csvFilePath != null && variableNames != null) {
			this.csvFilePath = csvFilePath;
			this.variableNames = variableNames;
		}
	}
}
