package com.bomber.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.ironrhino.core.fs.FileStorage;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;
import org.ironrhino.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.ApplicationInstance;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.util.ValueReplacer;
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

	@Value("${fileStorage.uri:file:///${app.context}/assets/}")
	protected URI uri;

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

	private static MultiValueMap<String, String> convertToHttpHeaders(List<HttpHeader> httpHeaderList) {
		if (httpHeaderList == null) {
			return null;
		}
		MultiValueMap<String, String> headers = new HttpHeaders();
		for (HttpHeader httpHeader : httpHeaderList) {
			headers.add(httpHeader.getName(), httpHeader.getValue());
		}
		return headers;
	}

	private static Map<String, String> parseFirstLine(InputStream inputStream, String[] fieldNames) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String firstLine = reader.readLine();

			if (firstLine == null || "".equals(firstLine)) {
				throw new IllegalArgumentException("文件首行为空");
			}

			String[] fieldValues = firstLine.split(",");
			if (fieldNames.length != fieldValues.length) {
				throw new IllegalArgumentException("变量数与文件列数不匹配");
			}
			Map<String, String> variables = new HashMap<>();
			for (int i = 0; i < fieldNames.length; i++) {
				variables.put(fieldNames[i], fieldValues[i]);
			}
			return variables;
		}
	}

	private static String convertToString(ResponseEntity<String> responseEntity) {
		MediaType contentType = responseEntity.getHeaders().getContentType();
		String body = responseEntity.getBody();
		if (contentType != null && body != null) {
			if (contentType.includes(MediaType.APPLICATION_JSON)) {
				return JsonUtils.prettify(body);
			} else if (contentType.includes(MediaType.TEXT_HTML) || contentType.includes(MediaType.TEXT_XML)) {
				List<Charset> charsets = responseEntity.getHeaders().getAcceptCharset();
				String charset = charsets.isEmpty() ? "utf-8" : charsets.get(0).name();
				return HtmlUtils.htmlEscape(body, charset);
			} else if (contentType.includes(MediaType.TEXT_PLAIN)) {
				return body;
			}
		}
		return "<" + responseEntity.getStatusCode().toString() + ' ' + responseEntity.getStatusCode().getReasonPhrase()
				+ ',' + responseEntity.getHeaders() + '>';
	}

	@Override
	@Transactional
	public String save() throws Exception {
		if (!makeEntityValid()) {
			return INPUT;
		}
		httpSample = getEntity();

		if (httpSample.getCsvFile() != null) {
			String fileName = httpSample.getCsvFileFileName();
			fileStorage.write(httpSample.getCsvFile(), fileName);

			httpSample.setCsvFilePath(fileName);
			logger.info("Upload file '{}'", fileName);
		}

		httpSample.setVariableNames(StringUtils.trimAllWhitespace(httpSample.getVariableNames()));

		httpSample.setBody(JsonUtils.prettify(httpSample.getBody()));

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

		List<Integer> numberOfThreadsList = Arrays.stream(threadGroup.split(", *")).map(Integer::parseInt).sorted()
				.collect(Collectors.toList());

		BomberContext ctx = new BomberContext();
		ctx.setSampleId(httpSample.getId());
		ctx.setName(name);
		ctx.setRequestsPerThread(requestsPerThread);
		ctx.setThreadGroup(numberOfThreadsList);
		bomberEngine.execute(ctx);

		addActionMessage("Bombing is ongoing!");

		threadGroupCache.put(httpSample.getId(),
				numberOfThreadsList.stream().map(i -> i + "").collect(Collectors.joining(", ")));
		requestsPerThreadCache.put(httpSample.getId(), requestsPerThread);
		return SUCCESS;
	}

	public String singleShot() {
		httpSample = httpSampleManager.get(this.getUid());
		String path = httpSample.getPath();
		ApplicationInstance app = httpSample.getApplicationInstance();
		try {
			URI uri = URI.create(app.getProtocol().name() + "://" + app.getHost() + ":" + app.getPort()
					+ (path.startsWith("/") ? path : "/" + path));
			HttpMethod method = httpSample.getMethod();
			MultiValueMap<String, String> headers = convertToHttpHeaders(httpSample.getHeaders());
			String body = httpSample.getBody();

			if (StringUtils.hasLength(body)) {
				String filePath = httpSample.getCsvFilePath();
				String fieldNames = httpSample.getVariableNames();
				if (StringUtils.hasLength(filePath) && StringUtils.hasLength(fieldNames)) {
					try (InputStream inputStream = fileStorage.open(filePath)) {
						if (inputStream == null) {
							throw new FileNotFoundException("文件不存在");
						}
						Map<String, String> variables = parseFirstLine(inputStream, fieldNames.split(","));
						body = ValueReplacer.replace(body, variables);
					}
				}
			}

			RequestEntity<String> requestEntity = new RequestEntity<>(body, headers, method, uri);
			logger.info("Request entity:\n{}", requestEntity.toString());

			ResponseEntity<String> responseEntity = stringMessageRestTemplate.exchange(requestEntity, String.class);
			String result = convertToString(responseEntity);
			addActionMessage(result);
			logger.info("Response entity:\n{}", result);
		} catch (HttpClientErrorException e) {
			// eg. 404 Not Found
			addActionError(e.getStatusCode().toString());
			logger.warn(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			addActionError(e.getMessage());
			logger.warn(e.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
}
