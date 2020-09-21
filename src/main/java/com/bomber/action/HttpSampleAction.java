package com.bomber.action;

import static com.bomber.util.ValueReplacer.containsReplaceableKeys;
import static com.bomber.util.ValueReplacer.replace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
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
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import com.bomber.engine.BomberContext;
import com.bomber.engine.BomberEngine;
import com.bomber.engine.Scope;
import com.bomber.functions.core.DefaultFunctionExecutor;
import com.bomber.functions.core.FunctionContext;
import com.bomber.functions.core.FunctionExecutor;
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.model.Payload;
import com.bomber.model.PayloadOption;
import com.bomber.util.FileUtils;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import lombok.Getter;
import lombok.Setter;

@AutoConfig(fileupload = "text/plain, text/csv")
public class HttpSampleAction extends EntityAction<HttpSample> {

	private static final long serialVersionUID = 6007215319135153063L;

	private static final Logger logger = LoggerFactory.getLogger(HttpSampleAction.class);

	private static final int MAX_REQUESTS_PRE_THREAD = 500;

	private static final String DEFAULT_THREAD_GROUP = "1, 2, 5, 10, 20, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500";

	private static final int DEFAULT_REQUESTS_PRE_THREAD = 10;

	private static final Map<String, String> threadGroupCache = new ConcurrentHashMap<>(16);

	private static final Map<String, Integer> requestsPerThreadCache = new ConcurrentHashMap<>(16);

	@Getter
	private final int maxRequestsPerThread = MAX_REQUESTS_PRE_THREAD;

	@Autowired
	private HttpSampleManager httpSampleManager;

	@Autowired
	private BomberEngine bomberEngine;

	@Autowired
	private RestTemplate stringMessageRestTemplate;

	@Autowired
	private FileStorage fileStorage;

	@Getter
	@Setter
	private HttpSample httpSample;

	@Setter
	@Getter
	private String threadGroup = DEFAULT_THREAD_GROUP;
	@Setter
	@Getter
	private int requestsPerThread = DEFAULT_REQUESTS_PRE_THREAD;
	@Getter
	@Setter
	private String name;
	@Setter
	private String scope;

	@Getter
	private boolean mutable = true;
	@Setter
	private int payloadIndex = 0;
	@Getter
	private Request request;
	@Getter
	private Response response;

	@Getter
	private String requestMessage;
	@Getter
	private String responseMessage;
	@Getter
	private String errorMessage;
	@Getter
	private long elapsedTimeInMillis;

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

	private static String convertToString(MultiValueMap<String, String> map) {
		StringJoiner joiner = new StringJoiner("\n");
		map.forEach(
				(key, value) -> joiner.add(key + ": " + (value.size() == 1 ? value.get(0) : String.join(", ", value))));
		return joiner.toString();
	}

	private static String convertToString(RequestEntity<String> requestEntity) {
		StringBuilder sb = new StringBuilder();

		sb.append(requestEntity.getMethod());
		sb.append(' ');
		sb.append(requestEntity.getUrl().toString());

		HttpHeaders httpHeaders = requestEntity.getHeaders();
		if (httpHeaders.size() > 0) {
			sb.append('\n');
			sb.append(convertToString(httpHeaders));
		}

		String body = requestEntity.getBody();
		if (body != null) {
			sb.append("\n\n");
			sb.append(requestEntity.getBody());
		}
		return sb.toString();
	}

	private static String convertToString(ResponseEntity<String> responseEntity) {
		StringBuilder sb = new StringBuilder();

		sb.append(responseEntity.getStatusCode().toString());

		HttpHeaders httpHeaders = responseEntity.getHeaders();
		if (httpHeaders.size() > 0) {
			sb.append('\n');
			sb.append(convertToString(httpHeaders));
		}

		String body = responseEntity.getBody();
		if (body != null) {
			MediaType contentType = responseEntity.getHeaders().getContentType();
			if (contentType != null) {
				if (contentType.includes(MediaType.APPLICATION_JSON)) {
					body = JsonUtils.prettify(body);
				} else if (contentType.includes(MediaType.TEXT_HTML) || contentType.includes(MediaType.TEXT_XML)) {
					List<Charset> charsets = responseEntity.getHeaders().getAcceptCharset();
					String charset = charsets.isEmpty() ? "utf-8" : charsets.get(0).name();
					body = HtmlUtils.htmlEscape(body, charset);
				}
			}
			sb.append("\n\n");
			sb.append(body);
		}
		return sb.toString();
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

	private static boolean isMutable(HttpSample httpSample) {
		if (containsReplaceableKeys(httpSample.getPath())) {
			return true;
		}
		for (HttpHeader header : httpSample.getHeaders()) {
			if (containsReplaceableKeys(header.getValue())) {
				return true;
			}
		}
		return containsReplaceableKeys(httpSample.getBody());
	}

	private static RequestEntity<String> createRequestEntity(HttpSample sample, Function<String, String> mapper) {
		URI uri = URI.create(mapper.apply(sample.getUrl()));
		HttpMethod method = sample.getMethod();
		MultiValueMap<String, String> headers = convertToHttpHeaders(sample.getHeaders(), mapper);
		String body = mapper.apply(sample.getBody());
		return new RequestEntity<>(body, headers, method, uri);
	}

	@Override
	@Transactional
	public String save() throws Exception {
		if (!makeEntityValid()) {
			return INPUT;
		}

		boolean deleteFile = httpSample.getCsvFile() == null && StringUtils.isEmpty(httpSample.getCsvFilePath());

		httpSample = getEntity();

		int i = 0;
		for (HttpHeader header : httpSample.getHeaders()) {
			if (header.getName() == null) {
				this.addFieldError("headers[" + i + "].name", "required");
				return INPUT;
			}
			if (header.getValue() == null) {
				this.addFieldError("headers[" + i + "].value", "required");
				return INPUT;
			}
			i++;

			if ("Content-Type".equals(header.getName()) && header.getValue().contains("json")) {
				httpSample.setBody(JsonUtils.prettify(httpSample.getBody()));
			}
		}

		if (httpSample.getCsvFile() != null) {
			String filePath = generateFilePath(httpSample.getCsvFileFileName());
			fileStorage.write(httpSample.getCsvFile(), filePath);

			httpSample.setCsvFilePath(filePath);
			logger.info("Upload file '{}'", filePath);
		}

		if (deleteFile && StringUtils.hasText(httpSample.getCsvFilePath())) {
			fileStorage.delete(httpSample.getCsvFilePath());
			httpSample.setCsvFilePath(null);
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
		threadGroup = threadGroupCache.getOrDefault(this.getUid(), DEFAULT_THREAD_GROUP);
		requestsPerThread = requestsPerThreadCache.getOrDefault(this.getUid(), DEFAULT_REQUESTS_PRE_THREAD);
		return "bombingPlan";
	}

	@Transactional
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

		List<Integer> numberOfThreadsList = Arrays.stream(threadGroup.trim().split(", *")).map(Integer::parseInt)
				.sorted().collect(Collectors.toList());

		BomberContext ctx = new BomberContext();
		ctx.setSampleId(httpSample.getId());
		ctx.setName(name);
		ctx.setRequestsPerThread(requestsPerThread);
		ctx.setThreadGroup(numberOfThreadsList);
		ctx.setScope(Scope.valueOf(scope));
		bomberEngine.execute(ctx);

		addActionMessage("Bombing is ongoing!");

		threadGroupCache.put(httpSample.getId(),
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
		if (httpSample.getPayload() != null) {
			Payload payload = httpSample.getPayload();
			List<FunctionContext> all = payload.getOptions().stream().map(PayloadOption::map)
					.collect(Collectors.toList());
			FunctionExecutor executor = new DefaultFunctionExecutor(all);
			try {
				return new DefaultFunctionExecutor(all).execute(index, 1).get(0);
			} finally {
				executor.shutdown();
			}
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
		this.mutable = isMutable(this.httpSample);
		if (this.mutable) {
			Map<String, String> context = getPayload(httpSample, this.payloadIndex);
			return createRequestEntity(httpSample, value -> replace(value, context));
		} else {
			return createRequestEntity(httpSample, String::toString);
		}
	}

	@Deprecated
	public String singleShot() {
		httpSample = httpSampleManager.get(this.getUid());
		Objects.requireNonNull(httpSample);
		try {
			RequestEntity<String> requestEntity = createRequestEntity();
			this.requestMessage = convertToString(requestEntity);
			logger.info("Request entity:\n{}", requestMessage);

			long startTime = System.nanoTime();
			ResponseEntity<String> responseEntity = stringMessageRestTemplate.exchange(requestEntity, String.class);
			this.elapsedTimeInMillis = (System.nanoTime() - startTime) / 1_000_000;

			this.responseMessage = convertToString(responseEntity);
			logger.info("Response entity:\n{}", responseMessage);
		} catch (HttpClientErrorException e) {
			// eg. 404 Not Found
			this.responseMessage = e.getStatusCode().toString() + "\n"
					+ HtmlUtils.htmlEscape(e.getResponseBodyAsString());
			logger.warn(e.getMessage());
		} catch (Exception e) {
			this.errorMessage = e.toString();
			logger.warn(e.getMessage());
		}
		return "singleShot";
	}

	@JsonConfig(root = "request")
	public String previewRequest() throws IOException {
		httpSample = httpSampleManager.get(this.getUid());
		Objects.requireNonNull(httpSample);
		RequestEntity<String> requestEntity = createRequestEntity();
		this.requestMessage = convertToString(requestEntity);
		this.request = new Request(this.requestMessage);
		return "json";
	}

	public String singleShotV2() {
		httpSample = httpSampleManager.get(this.getUid());
		Objects.requireNonNull(httpSample);
		try {
			RequestEntity<String> requestEntity = createRequestEntity();
			this.requestMessage = convertToString(requestEntity);
		} catch (Exception e) {
			this.errorMessage = e.toString();
			logger.warn(e.getMessage());
		}
		return "singleShotV2";
	}

	@JsonConfig(root = "response")
	public String executeRequest() throws IOException {
		httpSample = httpSampleManager.get(this.getUid());
		Objects.requireNonNull(httpSample);

		this.response = new Response();
		try {
			RequestEntity<String> requestEntity = createRequestEntity();
			String requestMessage = convertToString(requestEntity);
			logger.info("Request entity:\n{}", requestMessage);

			long startTime = System.nanoTime();
			ResponseEntity<String> responseEntity = stringMessageRestTemplate.exchange(requestEntity, String.class);
			long elapsedTimeInMillis = (System.nanoTime() - startTime) / 1_000_000;

			String responseMessage = convertToString(responseEntity);
			logger.info("Response entity:\n{}", responseMessage);
			response.setContent(responseMessage);
			response.setElapsedTimeInMillis(elapsedTimeInMillis);
		} catch (HttpClientErrorException e) {
			// eg. 404 Not Found
			response.setContent(
					e.getStatusCode().toString() + "\n" + HtmlUtils.htmlEscape(e.getResponseBodyAsString()));
			logger.warn(e.getMessage());
		} catch (Exception e) {
			response.setError(e.toString());
			logger.warn(e.getMessage());
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
