package com.bomber.service;

import com.bomber.entity.HttpSample;
import com.bomber.http.StringEntityFactory;
import com.bomber.http.StringEntityRender;
import com.bomber.mapper.HttpSampleMapper;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class HttpSampleService {

	private final HttpSampleMapper httpSampleMapper;

	private final HttpSampleExecutorService httpSampleExecutorService;

	private final FunctionExecutorService functionExecutorService;

	public HttpSampleService(HttpSampleMapper httpSampleMapper, HttpSampleExecutorService httpSampleExecutorService, FunctionExecutorService functionExecutorService) {
		this.httpSampleExecutorService = httpSampleExecutorService;
		this.httpSampleMapper = httpSampleMapper;
		this.functionExecutorService = functionExecutorService;
	}

	private Map<String, String> buildContext(HttpSample httpSample, int index) {
		return functionExecutorService.execute(httpSample.getGroupId(), index);
	}

	public List<Map<String, String>> buildContextList(HttpSample httpSample, int index, int size) {
		return functionExecutorService.execute(httpSample.getGroupId(), index, size);
	}

	public RequestEntity<String> createRequestEntity(int id, int index) {
		HttpSample httpSample = select(id);
		return createRequestEntity(httpSample, index);
	}

	public RequestEntity<String> createRequestEntity(HttpSample httpSample, int index) {
		return StringEntityFactory.create(httpSample, buildContext(httpSample, index));
	}

	public String renderRequest(int id, int index) {
		return StringEntityRender.renderPlainText(createRequestEntity(id, index));
	}

	public String renderRequest(HttpSample httpSample, int index) {
		return StringEntityRender.renderPlainText(createRequestEntity(httpSample, index));
	}

	public HttpSample select(int id) {
		return httpSampleMapper.select(id).orElseThrow(() -> new IllegalArgumentException("httpSample '" + id + "'"));
	}

	public void save(HttpSample httpSample) {
		httpSampleMapper.save(httpSample);
	}

	public HttpSampleResult execute(int id) {
		return httpSampleExecutorService.execute(select(id), Collections.emptyMap());
	}

	public HttpSampleResult execute(int id, int index) {
		HttpSample httpSample = select(id);
		return httpSampleExecutorService.execute(httpSample, buildContext(httpSample, index));
	}

	public List<HttpSampleResult> executeBatch(int id, int size) {
		HttpSample httpSample = select(id);
		List<HttpSampleResult> results = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			results.add(httpSampleExecutorService.execute(httpSample, Collections.emptyMap()));
		}
		return results;
	}

	public List<HttpSampleResult> executeBatch(int id, int index, int size) {
		HttpSample httpSample = select(id);
		List<HttpSampleResult> results = new ArrayList<>();
		for (Map<String, String> context : buildContextList(httpSample, index, size)) {
			results.add(httpSampleExecutorService.execute(httpSample, context));
		}
		return results;
	}

}
