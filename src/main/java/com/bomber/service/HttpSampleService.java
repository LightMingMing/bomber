package com.bomber.service;

import com.bomber.function.model.FunctionContext;
import com.bomber.function.runner.DefaultFunctionExecutor;
import com.bomber.http.StringEntityFactory;
import com.bomber.http.StringEntityRender;
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.FunctionDefinition;
import com.bomber.model.HttpSample;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class HttpSampleService {

	private final HttpSampleManager httpSampleManager;

	private final HttpSampleExecutorService httpSampleExecutorService;

	public HttpSampleService(HttpSampleManager httpSampleManager, HttpSampleExecutorService httpSampleExecutorService) {
		this.httpSampleExecutorService = httpSampleExecutorService;
		this.httpSampleManager = httpSampleManager;
	}

	public static Map<String, String> buildContext(HttpSample httpSample, int index) {
		if (httpSample.isMutable() && httpSample.getFunctionConfigure() != null) {
			List<FunctionContext> all = httpSample.getFunctionConfigure().getFunctionDefinitions()
				.stream().map(FunctionDefinition::map).collect(Collectors.toList());
			return new DefaultFunctionExecutor(all).execute(index);
		}
		return Collections.emptyMap();
	}

	public static List<Map<String, String>> buildContextList(HttpSample httpSample, int index, int size) {
		List<FunctionContext> all = httpSample.getFunctionConfigure().getFunctionDefinitions()
			.stream().map(FunctionDefinition::map).collect(Collectors.toList());
		return new DefaultFunctionExecutor(all).executeBatch(index, size);
	}

	public RequestEntity<String> createRequestEntity(String id, int index) {
		HttpSample httpSample = select(id);
		return createRequestEntity(httpSample, index);
	}

	public RequestEntity<String> createRequestEntity(HttpSample httpSample, int index) {
		return StringEntityFactory.create(httpSample, buildContext(httpSample, index));
	}

	public String renderRequest(String id, int index) {
		return StringEntityRender.renderPlainText(createRequestEntity(id, index));
	}

	public String renderRequest(HttpSample httpSample, int index) {
		return StringEntityRender.renderPlainText(createRequestEntity(httpSample, index));
	}

	public HttpSample select(String id) {
		return requireNonNull(httpSampleManager.get(id), "httpSample '" + id + "'");
	}

	public void save(HttpSample httpSample) {
		httpSampleManager.save(httpSample);
	}

	public HttpSampleResult execute(String id) {
		return httpSampleExecutorService.execute(select(id), Collections.emptyMap());
	}

	public HttpSampleResult execute(String id, int index) {
		HttpSample httpSample = select(id);
		return httpSampleExecutorService.execute(httpSample, buildContext(httpSample, index));
	}

	public List<HttpSampleResult> executeBatch(String id, int size) {
		HttpSample httpSample = select(id);
		List<HttpSampleResult> results = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			results.add(httpSampleExecutorService.execute(httpSample, Collections.emptyMap()));
		}
		return results;
	}

	public List<HttpSampleResult> executeBatch(String id, int index, int size) {
		HttpSample httpSample = select(id);
		List<HttpSampleResult> results = new ArrayList<>();
		for (Map<String, String> context : buildContextList(httpSample, index, size)) {
			results.add(httpSampleExecutorService.execute(httpSample, context));
		}
		return results;
	}

}
