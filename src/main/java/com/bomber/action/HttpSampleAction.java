package com.bomber.action;

import com.bomber.asserter.AssertResult;
import com.bomber.asserter.Asserter;
import com.bomber.asserter.util.Asserters;
import com.bomber.engine.model.Scope;
import com.bomber.http.StringEntityFactory;
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.Assertion;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.service.BomberRequest;
import com.bomber.service.BomberService;
import com.bomber.service.PayloadGenerateService;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import lombok.Getter;
import lombok.Setter;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.JsonConfig;
import org.ironrhino.core.struts.EntityAction;
import org.ironrhino.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.bomber.http.StringEntityRender.renderPlainText;
import static org.springframework.util.StringUtils.hasLength;

@AutoConfig(fileupload = "text/plain, text/csv")
public class HttpSampleAction extends EntityAction<HttpSample> {

	private static final long serialVersionUID = 6007215319135153063L;

	private static final Logger logger = LoggerFactory.getLogger(HttpSampleAction.class);

	private static final int MAX_REQUESTS_PRE_THREAD = 500;

	private static final String DEFAULT_THREAD_GROUPS = "1, 2, 5, 10, 20, 50, 100, 150, 200, 250";

	private static final int DEFAULT_REQUESTS_PRE_THREAD = 10;

	private static final Map<String, String> threadGroupsCache = new ConcurrentHashMap<>(16);

	private static final Map<String, Integer> requestsPerThreadCache = new ConcurrentHashMap<>(16);

	@Getter
	private final int maxRequestsPerThread = MAX_REQUESTS_PRE_THREAD;

	@Autowired
	private HttpSampleManager httpSampleManager;

	@Autowired
	private BomberService bomberService;

	@Autowired
	private PayloadGenerateService payloadGenerateService;

	@Autowired
	private RestTemplate stringMessageRestTemplate;

	@Getter
	@Setter
	private HttpSample httpSample;

	@Setter
	@Getter
	private String threadGroups = DEFAULT_THREAD_GROUPS;
	@Setter
	@Getter
	private int requestsPerThread = DEFAULT_REQUESTS_PRE_THREAD;
	@Getter
	@Setter
	private String name;
	@Setter
	private String scope;
	@Setter
	private int beginUserIndex = 0;
	@Setter
	private int iterations = 0;

	@Getter
	private int totalRequests;
	@Getter
	private int totalPayloads;

	@Setter
	private int from = 0;
	@Setter
	private int to;
	@Getter
	private Request request;
	@Getter
	private Response response;
	@Getter
	private List<Response> responses;

	@Getter
	private String requestMessage;
	@Getter
	private String errorMessage;

	private static AssertResult assertThat(String text, Assertion model) {
		Asserter asserter = Asserters.create(model.getAsserter());
		com.bomber.asserter.Assertion assertion = new com.bomber.asserter.Assertion();
		assertion.setCondition(model.getCondition());
		assertion.setExpression(model.getExpression());
		assertion.setExpected(model.getExpected());
		assertion.setText(text);
		return asserter.run(assertion);
	}

	public static AssertResult assertThat(String text, List<Assertion> models) {
		return models.stream().map(model -> assertThat(text, model)).filter(r -> !r.isSuccessful()).findFirst()
				.orElse(AssertResult.success());
	}

	private boolean headersValid() {
		List<HttpHeader> headers = httpSample.getHeaders();
		for (int i = 0; i < headers.size(); i++) {
			HttpHeader header = headers.get(i);
			if (header.getName() == null) {
				this.addFieldError("headers[" + i + "].name", "required");
				return false;
			}
			if (header.getValue() == null) {
				this.addFieldError("headers[" + i + "].value", "required");
				return false;
			}
		}
		return true;
	}

	// TODO validate json in front end
	private boolean isValidJson(String body) {
		try {
			JsonUtils.getObjectMapper().readTree(body);
			return true;
		} catch (JsonProcessingException e) {
			JsonLocation location = e.getLocation();
			String sb = e.getOriginalMessage() + "at [line: " + location.getLineNr() + "[column: "
					+ location.getColumnNr();
			this.addFieldError("body", sb);
			return false;
		}
	}

	@Override
	public String save() throws Exception {
		if (!makeEntityValid() || !headersValid()) {
			return INPUT;
		}

		httpSample = getEntity();

		if (hasLength(httpSample.getBody())) {
			for (HttpHeader header : httpSample.getHeaders()) {
				if (header.getName().equals("Content-Type") && header.getValue().contains("json")) {
					if (!isValidJson(httpSample.getBody())) {
						return INPUT;
					}
					httpSample.setBody(JsonUtils.prettify(httpSample.getBody()));
					break;
				}
			}
		}

		httpSampleManager.save(httpSample);
		return SUCCESS;
	}

	// shortcut to create
	public String quickCreate() {
		httpSample = httpSampleManager.get(this.getUid());
		httpSample.setId(null);
		return INPUT;
	}

	public String inputBombingPlan() {
		httpSample = httpSampleManager.get(this.getUid());
		threadGroups = threadGroupsCache.getOrDefault(this.getUid(), DEFAULT_THREAD_GROUPS);
		requestsPerThread = requestsPerThreadCache.getOrDefault(this.getUid(), DEFAULT_REQUESTS_PRE_THREAD);

		totalRequests = Arrays.stream(threadGroups.trim().split(", *")).map(Integer::parseInt).reduce(Integer::sum)
				.orElse(0) * requestsPerThread;
		totalPayloads = this.totalRequests; // default payload scope is request
		return "bombingPlan";
	}

	// Don't @Transactional
	@InputConfig(methodName = "inputBombingPlan")
	public String bomb() {
		if (name == null || "".equals(name)) {
			addFieldError("name", "name can't be empty");
			return ERROR;
		}

		httpSample = httpSampleManager.get(httpSample.getId());

		if (requestsPerThread > maxRequestsPerThread) {
			addFieldError("requestsPerThread", "requestsPerThread > " + maxRequestsPerThread);
			return ERROR;
		}

		List<Integer> numberOfThreadsList = Arrays.stream(threadGroups.trim().split(", *")).map(Integer::parseInt)
				.sorted().collect(Collectors.toList());

		BomberRequest request = new BomberRequest();
		request.setHttpSampleId(httpSample.getId());
		request.setName(name);
		request.setRequestsPerThread(requestsPerThread);
		request.setThreadGroups(numberOfThreadsList);
		request.setBeginUserIndex(beginUserIndex);
		request.setIterations(iterations);
		request.setScope(hasLength(scope) ? Scope.valueOf(scope) : Scope.Request);
		bomberService.execute(request);

		addActionMessage("Bombing is ongoing!");

		threadGroupsCache.put(httpSample.getId(),
				numberOfThreadsList.stream().map(i -> i + "").collect(Collectors.joining(", ")));
		requestsPerThreadCache.put(httpSample.getId(), requestsPerThread);
		return SUCCESS;
	}

	private Map<String, String> getPayload(HttpSample httpSample, int index) {
		if (httpSample.isMutable() && httpSample.getFunctionConfigure() != null) {
			return payloadGenerateService.generate(httpSample.getFunctionConfigure().getId(), index);
		} else {
			return Collections.emptyMap();
		}
	}

	private RequestEntity<String> createRequestEntity() {
		httpSample = Objects.requireNonNull(httpSampleManager.get(this.getUid()), "httpSample");
		return StringEntityFactory.create(httpSample, getPayload(httpSample, this.from));
	}

	public String singleShot() {
		try {
			requestMessage = renderPlainText(createRequestEntity());
		} catch (Exception e) {
			errorMessage = HtmlUtils.htmlEscape(e.toString());
			logger.warn(e.getMessage());
		}
		return "singleShot";
	}

	@JsonConfig(root = "request")
	public String previewRequest() throws IOException {
		request = new Request(renderPlainText(createRequestEntity()));
		return "json";
	}

	@JsonConfig(root = "response")
	public String executeRequest() {
		response = exchange(createRequestEntity());
		return "json";
	}

	private Response exchange(RequestEntity<String> requestEntity) {
		Response response = new Response();
		try {
			String requestMessage = renderPlainText(requestEntity);
			logger.info("Request entity:\n{}", requestMessage);

			long startTime = System.nanoTime();
			ResponseEntity<String> responseEntity = stringMessageRestTemplate.exchange(requestEntity, String.class);
			long elapsedTimeInMillis = (System.nanoTime() - startTime) / 1_000_000;

			List<Assertion> assertions = httpSample.getAssertions();
			if (assertions != null) {
				AssertResult result = assertThat(responseEntity.getBody(), assertions);
				if (!result.isSuccessful()) {
					logger.error("Assert failure: {}", result.getError());
					response.setError("Assert failure:\n\t" + result.getError());
				}
			}

			String responseMessage = renderPlainText(responseEntity);
			logger.info("Response entity:\n{}", responseMessage);
			response.setContent(responseMessage);
			response.setElapsedTimeInMillis(elapsedTimeInMillis);
		} catch (HttpClientErrorException e) {
			// eg. 404 Not Found
			response.setError(e.getStatusCode() + "\n" + HtmlUtils.htmlEscape(e.getResponseBodyAsString()));
			logger.error(e.getMessage());
		} catch (Exception e) {
			response.setError(e.toString());
			logger.error("execute request failed", e);
		}
		return response;
	}

	@JsonConfig(root = "responses")
	public String executeRequests() {
		httpSample = Objects.requireNonNull(httpSampleManager.get(this.getUid()), "httpSample");
		int count = this.to - this.from + 1;
		List<Map<String, String>> contextList = payloadGenerateService.generate(httpSample.getFunctionConfigure().getId(), this.from, count);

		responses = new ArrayList<>();
		for (Map<String, String> context : contextList) {
			responses.add(exchange(StringEntityFactory.create(httpSample, context)));
		}
		return "json";
	}

	@Getter
	@Setter
	static class Request {

		private String content;

		public Request(String content) {
			this.content = content;
		}
	}

	@Getter
	@Setter
	static class Response {

		private String content;

		private String error;

		private long elapsedTimeInMillis;

	}
}
