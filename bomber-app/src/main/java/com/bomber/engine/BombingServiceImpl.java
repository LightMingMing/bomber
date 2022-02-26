package com.bomber.engine;

import static com.bomber.entity.Status.FAILURE;
import static com.bomber.entity.Status.PAUSE;
import static com.bomber.entity.Status.READY;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bomber.controller.FunctionController;
import com.bomber.engine.model.BomberRequest;
import com.bomber.engine.model.HttpRequest;
import com.bomber.engine.model.Payload;
import com.bomber.entity.HttpHeader;
import com.bomber.entity.HttpSample;
import com.bomber.entity.TestingRecord;
import com.bomber.mapper.HttpSampleMapper;
import com.bomber.mapper.TestingRecordMapper;

@Service
public class BombingServiceImpl implements BombingService {

	private static String HOST_ADDRESS;

	private final HttpSampleMapper httpSampleMapper;

	private final TestingRecordMapper testingRecordMapper;

	private final BomberEngine bomberEngine;

	@Value("${server.port:8080}")
	private String port;

	public BombingServiceImpl(HttpSampleMapper httpSampleMapper, TestingRecordMapper testingRecordMapper,
							  BomberEngine bomberEngine) {
		this.httpSampleMapper = httpSampleMapper;
		this.testingRecordMapper = testingRecordMapper;
		this.bomberEngine = bomberEngine;
	}

	private static TestingRecord createTestingRecord(BombingRequest request) {
		TestingRecord record = new TestingRecord();
		record.setName(request.getName());
		record.setThreadGroups(request.getThreadGroups());
		record.setRequestsPerThread(request.getRequestsPerThread());
		record.setHttpSampleId(request.getHttpSampleId());
		record.setScope(request.getScope());
		record.setBeginUserIndex(request.getBeginUserIndex());
		record.setIterations(request.getIterations());
		record.setCreateTime(new Date());
		record.setStatus(READY);
		return record;
	}

	private static List<String> convertTo(List<HttpHeader> headers) {
		return headers.stream().map(header -> header.getName() + ":" + header.getValue())
			.collect(Collectors.toList());
	}

	private static HttpRequest createHttpRequest(HttpSample sample) {
		HttpRequest request = new HttpRequest();
		request.setMethod(sample.getMethod());
		request.setUrl(sample.getUrl());
		request.setBody(sample.getBody());
		request.setHeaders(convertTo(sample.getHeaders()));

		sample.getAssertions().forEach(each -> request.addAssertion(each.getAsserter(),
			each.getExpression(), each.getCondition().name(), each.getExpected()));
		return request;
	}

	public static String getHostAddress() {
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = e.nextElement();
				Enumeration<InetAddress> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress inetAddress = ee.nextElement();
					if (inetAddress instanceof Inet4Address) {
						String address = inetAddress.getHostAddress();
						if (address.equals("127.0.0.1") || address.startsWith("169.254.")) {
							continue;
						}
						return address;
					}
				}
			}
			return "127.0.0.1";
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

	private BomberRequest createBomberRequest(TestingRecord record, HttpSample httpSample) {
		BomberRequest request = new BomberRequest();
		request.setId(record.getId());
		request.setHttpRequest(createHttpRequest(httpSample));
		request.setName(record.getName());
		request.setThreadGroups(record.getThreadGroups());
		request.setThreadGroupCursor(record.getThreadGroupCursor());
		request.setRequestsPerThread(record.getRequestsPerThread());
		request.setIterations(record.getIterations());
		request.setIteration(record.getCurrentIteration());

		if (httpSample.containsVariables()) {
			Payload payload = new Payload();
			payload.setUrl(getPayloadApiUrl(httpSample.getGroupId()));
			payload.setScope(record.getScope());
			payload.setStart(record.getBeginUserIndex());
			request.setPayload(payload);
		}
		return request;
	}

	private String getPayloadApiUrl(Integer workspaceId) {
		if (HOST_ADDRESS == null) {
			HOST_ADDRESS = getHostAddress();
		}
		return "http://" + HOST_ADDRESS + ":" + port + FunctionController.API + "/" + workspaceId;
	}

	@Override
	public Long execute(@NonNull BombingRequest request) {
		Optional<HttpSample> httpSampleOptional = httpSampleMapper.select(request.getHttpSampleId());
		if (httpSampleOptional.isEmpty()) {
			return -1L;
		}
		TestingRecord record = createTestingRecord(request);
		testingRecordMapper.save(record);

		execute(httpSampleOptional.get(), record);
		return record.getId();
	}

	@Override
	public Long continueExecute(@NonNull Long id) {
		Optional<TestingRecord> recordOptional = testingRecordMapper.select(id);
		if (recordOptional.isEmpty()) {
			return -1L;
		}
		TestingRecord record = recordOptional.get();

		Optional<HttpSample> httpSampleOptional = httpSampleMapper.select(record.getHttpSampleId());
		if (httpSampleOptional.isEmpty()) {
			throw new IllegalArgumentException(
				"Not found httpSample '" + record.getHttpSampleId() + "' (Dirty Data)");
		}

		if (record.getStatus() != PAUSE && record.getStatus() != FAILURE) {
			return 0L;
		}
		record.setStatus(READY);
		testingRecordMapper.save(record);

		execute(httpSampleOptional.get(), record);
		return id;
	}

	private void execute(HttpSample httpSample, TestingRecord record) {
		bomberEngine.execute(createBomberRequest(record, httpSample));
	}

	@Override
	public void pauseExecute(@NonNull Long id) {
		bomberEngine.pause(id);
	}
}
