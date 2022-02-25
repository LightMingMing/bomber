package com.bomber.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpSampleResult {

	private String content;

	private String error;

	private long elapsedTimeInMillis;

}