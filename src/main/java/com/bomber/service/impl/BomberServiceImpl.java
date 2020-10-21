package com.bomber.service.impl;

import static com.bomber.api.controller.PayloadController.getPayloadApiUrl;
import static com.bomber.model.BombingStatus.FAILURE;
import static com.bomber.model.BombingStatus.PAUSE;
import static com.bomber.model.BombingStatus.READY;
import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bomber.converter.HttpHeaderListConverter;
import com.bomber.engine.BomberContext;
import com.bomber.engine.BomberEngine;
import com.bomber.engine.HttpSampleSnapshot;
import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.HttpSampleManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import com.bomber.service.BomberRequest;
import com.bomber.service.BomberService;

@Service
public class BomberServiceImpl implements BomberService {

	private final HttpSampleManager httpSampleManager;

	private final BombingRecordManager bombingRecordManager;

	private final BomberEngine bomberEngine;

	@Value("${fileStorage.uri}")
	protected URI uri;

	public BomberServiceImpl(HttpSampleManager httpSampleManager, BombingRecordManager bombingRecordManager,
			BomberEngine bomberEngine) {
		this.httpSampleManager = httpSampleManager;
		this.bombingRecordManager = bombingRecordManager;
		this.bomberEngine = bomberEngine;
	}

	private static BombingRecord createBombingRecord(BomberRequest request, HttpSample httpSample) {
		BombingRecord record = new BombingRecord();
		record.setName(request.getName());
		record.setThreadGroups(request.getThreadGroups());
		record.setRequestsPerThread(request.getRequestsPerThread());
		record.setHttpSample(httpSample);
		record.setScope(request.getScope());
		record.setStartPayloadIndex(request.getPayloadIndex());
		record.setCreateTime(new Date());
		record.setStatus(READY);
		return record;
	}

	public static List<String> convertToStringList(List<HttpHeader> headers) {
		return headers.stream().map(HttpHeaderListConverter::convertToString).collect(Collectors.toList());
	}

	public HttpSampleSnapshot createHttpSampleSnapshot(HttpSample sample) {
		HttpSampleSnapshot snapshot = new HttpSampleSnapshot();
		snapshot.setMethod(sample.getMethod());
		snapshot.setUrl(sample.getUrl());
		snapshot.setBody(sample.getBody());
		snapshot.setHeaders(convertToStringList(sample.getHeaders()));
		sample.getAssertions().forEach(each -> snapshot.addAssertion(each.getAsserter(), each.getExpression(),
				each.getCondition().name(), each.getExpected()));

		if (sample.isMutable()) {
			if (sample.getPayload() != null) {
				snapshot.setPayloadUrl(getPayloadApiUrl(sample.getPayload().getId()));
				snapshot.setVariableNames(snapshot.readVariables());
			} else {
				String payloadFile = snapshot.getPayloadFile();
				String variableNames = snapshot.getVariableNames();
				if (StringUtils.hasLength(payloadFile) && StringUtils.hasLength(variableNames)) {
					snapshot.setPayloadFile(uri.getPath() + payloadFile);
					snapshot.setVariableNames(variableNames);
				}
			}
		}
		return snapshot;
	}

	private BomberContext createBomberContext(BombingRecord record) {
		BomberContext ctx = new BomberContext(record.getId());
		ctx.setHttpSampleSnapshot(createHttpSampleSnapshot(record.getHttpSample()));
		ctx.setName(record.getName());
		ctx.setThreadGroups(record.getThreadGroups());
		ctx.setThreadGroupCursor(record.getThreadGroupCursor());
		ctx.setRequestsPerThread(record.getRequestsPerThread());
		ctx.setActiveThreads(record.getActiveThreads());
		ctx.setScope(record.getScope());
		ctx.setStart(record.getStartPayloadIndex());
		return ctx;
	}

	@Override
	public void execute(@NonNull BomberRequest request) {
		HttpSample httpSample = requireNonNull(httpSampleManager.get(request.getHttpSampleId()), "httpSample");

		BombingRecord record = createBombingRecord(request, httpSample);
		bombingRecordManager.save(record);

		bomberEngine.execute(createBomberContext(record));
	}

	@Override
	public void continueExecute(@NonNull String id) {
		BombingRecord record = bombingRecordManager.get(id);
		if (record == null || (record.getStatus() != PAUSE && record.getStatus() != FAILURE)) {
			return;
		}
		record.setStatus(READY);
		bombingRecordManager.save(record);

		bomberEngine.execute(createBomberContext(record));
	}

	@Override
	public void pauseExecute(@NonNull String id) {
		bomberEngine.pauseExecute(id);
	}
}
