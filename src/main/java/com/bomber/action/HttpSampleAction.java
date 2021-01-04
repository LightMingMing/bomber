package com.bomber.action;

import static com.bomber.http.StringEntityRender.renderPlainText;
import static com.bomber.util.ValueReplacer.replace;
import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ironrhino.core.fs.FileStorage;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.JsonConfig;
import org.ironrhino.core.struts.EntityAction;
import org.ironrhino.core.util.CodecUtils;
import org.ironrhino.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import com.bomber.asserter.AssertResult;
import com.bomber.asserter.Asserter;
import com.bomber.asserter.util.Asserters;
import com.bomber.engine.Scope;
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.Assertion;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.service.BomberRequest;
import com.bomber.service.BomberService;
import com.bomber.service.PayloadGenerateService;
import com.bomber.util.FileUtils;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import lombok.Getter;
import lombok.Setter;

@AutoConfig(fileupload = "text/plain, text/csv")
public class HttpSampleAction extends EntityAction<HttpSample> {

	private static final long serialVersionUID = 6007215319135153063L;

	private static final Logger logger = LoggerFactory.getLogger(HttpSampleAction.class);

	private static final int MAX_REQUESTS_PRE_THREAD = 500;

	private static final String DEFAULT_THREAD_GROUPS = "1, 2, 5, 10, 20, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500";

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

	@Autowired
	private FileStorage fileStorage;

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
	private int startPayloadIndex = 0;

	@Getter
	private int totalRequests;
	@Getter
	private int totalPayloads;

	@Getter
	private boolean mutable = true;
	@Setter
	private int userIndex = 0;
	@Getter
	private Request request;
	@Getter
	private Response response;

	@Getter
	private String requestMessage;
	@Getter
	private String errorMessage;

	private static MultiValueMap<String, String> convertToHttpHeaders(List<HttpHeader> httpHeaderList,
			Function<String, String> mapper) {
		if (httpHeaderList == null) {
			return null;
		}
		MultiValueMap<String, String> headers = new HttpHeaders();
		for (HttpHeader httpHeader : httpHeaderList) {
			headers.add(httpHeader.getName(), mapper.apply(httpHeader.getValue()));
		}
		return headers;
	}

	private static String generateFilePath(String fileName) {
		int extIndex = fileName.lastIndexOf('.');
		String prefix, suffix;
		if (extIndex > 0) {
			prefix = fileName.substring(0, extIndex);
			suffix = fileName.substring(extIndex);
		} else {
			prefix = fileName;
			suffix = ".txt";
		}
		return prefix + CodecUtils.nextId(4) + suffix;
	}

	private static RequestEntity<String> createRequestEntity(HttpSample sample, Function<String, String> mapper) {
		URI uri = URI.create(mapper.apply(sample.getUrl()));
		HttpMethod method = sample.getMethod();
		MultiValueMap<String, String> headers = convertToHttpHeaders(sample.getHeaders(), mapper);
		String body = mapper.apply(sample.getBody());
		return new RequestEntity<>(body, headers, method, uri);
	}

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

	// return true if not upload new file and file path is set to empty
	private boolean shouldDeleteOldFile() {
		return isNull(httpSample.getCsvFile()) && isEmpty(httpSample.getCsvFilePath());
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

		boolean shouldDelete = shouldDeleteOldFile();
		httpSample = getEntity();
		if (shouldDelete && !isEmpty(httpSample.getCsvFilePath())) {
			fileStorage.delete(httpSample.getCsvFilePath());
			httpSample.setCsvFilePath(null);
		}

		if (!isEmpty(httpSample.getBody())) {
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

		// upload file
		if (httpSample.getCsvFile() != null) {
			String filePath = generateFilePath(httpSample.getCsvFileFileName());
			fileStorage.write(httpSample.getCsvFile(), filePath);
			httpSample.setCsvFilePath(filePath);
			logger.info("Upload file '{}'", filePath);
		}

		httpSample.setVariableNames(StringUtils.trimAllWhitespace(httpSample.getVariableNames()));

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
		mutable = httpSample.isMutable();
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
		request.setPayloadIndex(startPayloadIndex);
		request.setScope(StringUtils.isEmpty(scope) ? Scope.Request : Scope.valueOf(scope));
		bomberService.execute(request);

		addActionMessage("Bombing is ongoing!");

		threadGroupsCache.put(httpSample.getId(),
				numberOfThreadsList.stream().map(i -> i + "").collect(Collectors.joining(", ")));
		requestsPerThreadCache.put(httpSample.getId(), requestsPerThread);
		return SUCCESS;
	}

	private Map<String, String> readVariablesFromFile(String filePath, int lineNumber, String[] variableNames)
			throws IOException {
		try (InputStream inputStream = fileStorage.open(filePath)) {
			if (inputStream == null) {
				throw new FileNotFoundException("文件不存在");
			}
			String line = FileUtils.readSpecificLine(inputStream, lineNumber);
			String[] values = line.trim().split(", *");
			if (values.length != variableNames.length) {
				throw new IllegalArgumentException("变量数与文件列数不匹配");
			}
			Map<String, String> variables = new HashMap<>();
			for (int i = 0; i < variableNames.length; i++) {
				variables.put(variableNames[i], values[i]);
			}
			return variables;
		}
	}

	private Map<String, String> getPayload(HttpSample httpSample, int index) throws IOException {
		if (httpSample.getFunctionConfigure() != null) {
			return payloadGenerateService.generate(httpSample.getFunctionConfigure().getId(), index);
		} else {
			String filePath = httpSample.getCsvFilePath();
			String names = httpSample.getVariableNames();
			if (StringUtils.hasLength(filePath) && StringUtils.hasLength(names)) {
				return readVariablesFromFile(filePath, index, names.split(", *"));
			}
			return Collections.emptyMap();
		}
	}

	private RequestEntity<String> createRequestEntity() throws IOException {
		httpSample = Objects.requireNonNull(httpSampleManager.get(this.getUid()), "httpSample");
		if (mutable = httpSample.isMutable()) {
			Map<String, String> context = getPayload(httpSample, this.userIndex);
			return createRequestEntity(httpSample, value -> replace(value, context));
		} else {
			// String::toString will cause NPE if value is null
			return createRequestEntity(httpSample, value -> value);
		}
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
		response = new Response();
		try {
			RequestEntity<String> requestEntity = createRequestEntity();
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
			response.setError(e.getStatusCode().toString() + "\n" + HtmlUtils.htmlEscape(e.getResponseBodyAsString()));
			logger.error(e.getMessage());
		} catch (Exception e) {
			response.setError(e.toString());
			logger.error("execute request failed", e);
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
