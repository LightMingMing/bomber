package com.bomber.rpc;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BombardierRequest {
	@JsonProperty("numConns")
	private int numberOfConnections;

	@JsonProperty("numReqs")
	private int numberOfRequests;

	@JsonProperty("method")
	private HttpMethod method;

	private List<String> headers;

	private String url;

	private String body;

	private String payloadFile;

	private String payloadUrl;

	private String variableNames;

	private int startLine;

	private String scope;
}
