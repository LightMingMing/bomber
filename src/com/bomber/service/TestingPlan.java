package com.bomber.service;

import com.bomber.converter.HttpHeaderListConverter;
import com.bomber.model.HttpSample;
import com.bomber.model.RequestMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestingPlan {

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

	public TestingPlan() {
	}

	public TestingPlan(HttpSample httpSample) {
		this(httpSample, 1, 1);
	}

	public TestingPlan(HttpSample httpSample, int threads, int requests) {
		this(httpSample, "", threads, requests, 0);
	}

	public TestingPlan(HttpSample httpSample, String rootPath, int threads, int requests, int startLine) {
		this.body = httpSample.getBody();
		this.url = httpSample.getUrl();
		this.method = httpSample.getMethod();
		if (httpSample.getVariableNames() != null && httpSample.getCsvFilePath() != null) {
			this.csvFilePath = rootPath + httpSample.getCsvFilePath();
			this.variableNames = httpSample.getVariableNames();
			this.startLine = startLine;
		}
		this.headers = HttpHeaderListConverter.convertToString(httpSample.getHeaders());
		this.numberOfConnections = threads;
		this.numberOfRequests = requests;
	}
}
