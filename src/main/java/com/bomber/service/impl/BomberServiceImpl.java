package com.bomber.service.impl;

import static com.bomber.api.controller.PayloadController.getPayloadApiUrl;
import static com.bomber.model.BombingStatus.FAILURE;
import static com.bomber.model.BombingStatus.PAUSE;
import static com.bomber.model.BombingStatus.READY;
import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bomber.converter.HttpHeaderListConverter;
import com.bomber.engine.BomberEngine;
import com.bomber.engine.model.BomberRequest;
import com.bomber.engine.model.HttpRequest;
import com.bomber.engine.model.Payload;
import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.service.BomberService;

@Service
public class BomberServiceImpl implements BomberService {

	private final HttpSampleManager httpSampleManager;

	private final BombingRecordManager bombingRecordManager;

	private final BomberEngine bomberEngine;

	public BomberServiceImpl(HttpSampleManager httpSampleManager, BombingRecordManager bombingRecordManager,
							 BomberEngine bomberEngine) {
		this.httpSampleManager = httpSampleManager;
		this.bombingRecordManager = bombingRecordManager;
		this.bomberEngine = bomberEngine;
	}

	private static BombingRecord createBombingRecord(com.bomber.service.BomberRequest request, HttpSample httpSample) {
		BombingRecord record = new BombingRecord();
		record.setName(request.getName());
		record.setThreadGroups(request.getThreadGroups());
		record.setRequestsPerThread(request.getRequestsPerThread());
		record.setHttpSample(httpSample);
		record.setScope(request.getScope());
		record.setBeginUserIndex(request.getBeginUserIndex());
		record.setIterations(request.getIterations());
		record.setCreateTime(new Date());
		record.setStatus(READY);
		return record;
	}

	public static List<String> convertToStringList(List<HttpHeader> headers) {
		return headers.stream().map(HttpHeaderListConverter::convertToString).collect(Collectors.toList());
	}

	public HttpRequest createHttpRequest(HttpSample sample) {
		HttpRequest snapshot = new HttpRequest();
		snapshot.setMethod(sample.getMethod());
		snapshot.setUrl(sample.getUrl());
		snapshot.setBody(sample.getBody());
		snapshot.setHeaders(convertToStringList(sample.getHeaders()));
		// history httpSample
		if (sample.getAssertions() != null) {
			sample.getAssertions().forEach(each -> snapshot.addAssertion(each.getAsserter(), each.getExpression(),
					each.getCondition().name(), each.getExpected()));
		}
		return snapshot;
	}

	private BomberRequest createBomberRequest(BombingRecord record) {
		HttpSample httpSample = record.getHttpSample();
		BomberRequest request = new BomberRequest();
		request.setId(record.getId());
		request.setHttpRequest(createHttpRequest(httpSample));
		request.setName(record.getName());
		request.setThreadGroups(record.getThreadGroups());
		request.setThreadGroupCursor(record.getThreadGroupCursor());
		request.setRequestsPerThread(record.getRequestsPerThread());
		request.setIterations(record.getIterations());
		request.setIteration(record.getCurrentIterations());

		if (httpSample.isMutable() && httpSample.getFunctionConfigure() != null) {
			Payload payload = new Payload();
			payload.setUrl(getPayloadApiUrl(httpSample.getFunctionConfigure().getId()));
			payload.setScope(record.getScope());
			payload.setStart(record.getBeginUserIndex());
			request.setPayload(payload);
		}
		return request;
	}

	@Override
	public void execute(@NonNull com.bomber.service.BomberRequest request) {
		HttpSample httpSample = requireNonNull(httpSampleManager.get(request.getHttpSampleId()), "httpSample");

		BombingRecord record = createBombingRecord(request, httpSample);
		bombingRecordManager.save(record);

		bomberEngine.execute(createBomberRequest(record));
	}

	@Override
	public void continueExecute(@NonNull String id) {
		BombingRecord record = bombingRecordManager.get(id);
		if (record == null || (record.getStatus() != PAUSE && record.getStatus() != FAILURE)) {
			return;
		}
		record.setStatus(READY);
		bombingRecordManager.save(record);

		bomberEngine.execute(createBomberRequest(record));
	}

	@Override
	public void pauseExecute(@NonNull String id) {
		bomberEngine.pause(id);
	}
}
