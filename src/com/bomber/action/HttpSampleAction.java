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
import java.util.Objects;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bomber.engine.BomberContext;
import com.bomber.engine.BomberEngine;
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.util.ValueReplacer;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.HtmlUtils;

@AutoConfig(fileupload = "text/plain, text/csv")
public class HttpSampleAction extends EntityAction<HttpSample> {

	private static final long serialVersionUID = 6007215319135153063L;

	private static final Logger logger = LoggerFactory.getLogger(HttpSampleAction.class);

	private static final int MAX_REQUESTS_PRE_THREAD = 500;

	private static final int DEFAULT_REQUESTS_PRE_THREAD = 10;

	private static String lastThreadGroup = "1, 2, 5, 10, 20, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500";

	private static int lastRequestsPerThread = DEFAULT_REQUESTS_PRE_THREAD;

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
	private String threadGroup = lastThreadGroup;
	@Setter
	@Getter
	private int requestsPerThread = lastRequestsPerThread;
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

	private static String displayResponseEntity(ResponseEntity<String> responseEntity) {
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

	public String inputBombingPlan() {
		httpSample = httpSampleManager.get(this.getUid());
		return "bombingPlan";
	}

	@Transactional
	@InputConfig(methodName = "inputBombingPlan")
	public String bombing() {
		if (name == null || "".equals(name)) {
			addFieldError("name", "name can't be empty");
			return ERROR;
		}

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

		lastThreadGroup = numberOfThreadsList.stream().map(i -> i + "").collect(Collectors.joining(", "));
		lastRequestsPerThread = requestsPerThread;
		return SUCCESS;
	}

	public String singleShot() {
		httpSample = httpSampleManager.get(this.getUid());
		try {
			URI uri = URI.create(httpSample.getUrl());
			HttpMethod method = HttpMethod.valueOf(httpSample.getMethod().name());
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

			RequestEntity<String> entity = new RequestEntity<>(body, headers, method, uri);

			ResponseEntity<String> result = stringMessageRestTemplate.exchange(entity, String.class);
			addActionMessage(displayResponseEntity(result));
		} catch (HttpClientErrorException e) {
			// eg. 404 Not Found
			addActionError(e.getStatusCode().toString());
			return ERROR;
		} catch (Exception e) {
			addActionError(e.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
}
