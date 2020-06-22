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

	private String[] headers;

	private String url;

	private String body;

	private String csvFilePath;

	private String variableNames;

	private int startLine;
}
