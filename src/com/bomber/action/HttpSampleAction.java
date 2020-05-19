package com.bomber.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ironrhino.core.fs.FileStorage;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;
import org.ironrhino.core.util.DateUtils;
import org.ironrhino.core.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.bomber.manager.HttpSampleManager;
import com.bomber.manager.TestingRecordManager;
import com.bomber.model.HttpSample;
import com.bomber.model.TestingRecord;
import com.bomber.service.BombardierService;
import com.bomber.service.TestingPlan;
import com.bomber.service.TestingResult;
import com.bomber.util.ValueReplacer;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AutoConfig(fileupload = "text/plain, text/csv")
public class HttpSampleAction extends EntityAction<HttpSample> {

	private final static Logger logger = LoggerFactory.getLogger(HttpSampleAction.class);

	private static String lastThreadGroup = "1, 2, 5, 10, 20, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500";

	@Value("${fileStorage.uri:file:///${app.context}/assets/}")
	protected URI uri;

	@Autowired
	private HttpSampleManager httpSampleManager;
	@Autowired
	private TestingRecordManager testingRecordManager;
	@Autowired
	private BombardierService bombardierService;
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
	private int requestsPerThread = 10;

	private static MultiValueMap<String, String> parseHttpHeaders(List<String> httpHeadersInText) {
		MultiValueMap<String, String> headers = new HttpHeaders();
		for (String header : httpHeadersInText) {
			String key = header.substring(0, header.indexOf(':'));
			String value = header.substring(header.indexOf(':') + 1);
			headers.add(key, value);
		}
		return headers;
	}

	private static TestingRecord makeTestingRecord(TestingResult result) {
		TestingRecord record = new TestingRecord();
		record.setNumberOfThreads(result.getNumConns());
		record.setNumberOfRequests(result.getNumReqs());
		record.setTps(result.getTps());
		record.setStatusStats(result.getStatus());
		record.setLatencyStats(result.getLatency());
		return record;
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

	private static String generatePath(String fileName) {
		int extIndex = fileName.lastIndexOf('.');
		String prefix, suffix;
		if (extIndex != -1) {
			prefix = fileName.substring(0, extIndex);
			suffix = fileName.substring(extIndex);
		} else {
			prefix = fileName;
			suffix = ".txt";
		}
		return prefix + DateUtils.format(new Date(), "yyMMddHHmmss") + suffix;
	}

	@Override
	@Transactional
	public String save() throws Exception {
		if (!makeEntityValid()) {
			return INPUT;
		}
		httpSample = getEntity();

		if (httpSample.getCsvFile() != null) {
			String path = generatePath(httpSample.getCsvFileFileName());
			fileStorage.write(httpSample.getCsvFile(), path);

			httpSample.setCsvFilePath(path);
			logger.info("Upload file '{}'", path);
		}

		httpSample.setVariableNames(StringUtils.trimAllWhitespace(httpSample.getVariableNames()));

		httpSample.setBody(JsonUtils.prettify(httpSample.getBody()));

		httpSampleManager.save(httpSample);
		return SUCCESS;
	}

	public String inputTestingPlan() {
		httpSample = httpSampleManager.get(this.getUid());
		return "testingPlan";
	}

	@Transactional
	@InputConfig(methodName = "inputTestingPlan")
	public String benchmark() {
		List<Integer> numberOfThreadsList = Arrays.stream(threadGroup.split(", *")).map(Integer::parseInt).sorted()
				.collect(Collectors.toList());

		int requestCount = 0;
		httpSample = httpSampleManager.get(httpSample.getId());

		List<SummaryReport> summaryReportList = new ArrayList<>();
		for (int numberOfThreads : numberOfThreadsList) {
			int numberOfRequests = numberOfThreads * requestsPerThread;
			TestingPlan testingPlan = new TestingPlan(httpSample, uri.getPath(), numberOfThreads, numberOfRequests,
					requestCount);

			TestingResult result = bombardierService.execute(testingPlan);

			TestingRecord testingRecord = makeTestingRecord(result);
			testingRecord.setHttpSample(httpSample);
			testingRecordManager.save(testingRecord);

			summaryReportList.add(new SummaryReport(numberOfThreads, result.getTps()));

			requestCount += numberOfRequests;
		}
		addActionMessage(JsonUtils.prettify(JsonUtils.toJson(summaryReportList)));

		lastThreadGroup = numberOfThreadsList.stream().map(i -> i + "").collect(Collectors.joining(", "));
		return SUCCESS;
	}

	public String singleShot() {
		httpSample = httpSampleManager.get(this.getUid());
		try {
			URI uri = URI.create(httpSample.getUrl());
			HttpMethod method = HttpMethod.valueOf(httpSample.getMethod().name());
			MultiValueMap<String, String> headers = parseHttpHeaders(httpSample.getHeaders());
			String body = httpSample.getBody();

			if (StringUtils.hasLength(body)) {
				String filePath = httpSample.getCsvFilePath();
				String fieldNames = httpSample.getVariableNames();
				if (StringUtils.hasLength(filePath) && StringUtils.hasLength(fieldNames)) {
					try (InputStream inputStream = fileStorage.open(filePath)) {
						Map<String, String> variables = parseFirstLine(inputStream, fieldNames.split(","));
						body = ValueReplacer.replace(body, variables);
					}
				}
			}

			RequestEntity<String> entity = new RequestEntity<>(body, headers, method, uri);

			ResponseEntity<String> result = stringMessageRestTemplate.exchange(entity, String.class);

			addActionMessage(JsonUtils.prettify(result.getBody()));
		} catch (Exception e) {
			addActionError(e.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class SummaryReport {
		private int numberOfThreads;
		private double tps;
	}
}
