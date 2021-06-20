package com.bomber.engine;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.engine.model.Result;
import com.bomber.engine.monitor.TestingEvent;
import com.bomber.engine.monitor.TestingListener;
import com.bomber.entity.Status;
import com.bomber.entity.SummaryReport;
import com.bomber.entity.TestingRecord;
import com.bomber.mapper.SummaryReportMapper;
import com.bomber.mapper.TestingRecordMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试监听
 *
 * @author MingMing Zhao
 */
@Slf4j
@Component
public class BomberTestingListener implements TestingListener {

	private final TestingRecordMapper testingRecordMapper;

	private final SummaryReportMapper summaryReportMapper;

	public BomberTestingListener(TestingRecordMapper testingRecordMapper,
								 SummaryReportMapper summaryReportMapper) {
		this.testingRecordMapper = testingRecordMapper;
		this.summaryReportMapper = summaryReportMapper;
	}

	@Override
	@Transactional
	public boolean started(TestingEvent event) {
		Optional<TestingRecord> optional = testingRecordMapper.select(event.getId());
		if (optional.isEmpty()) {
			return false;
		}
		TestingRecord record = optional.get();
		if (record.getStartTime() == null) {
			record.setStartTime(new Date());
		}
		record.setStatus(Status.RUNNING);
		testingRecordMapper.save(record);
		return true;
	}

	@Override
	@Transactional
	public void paused(TestingEvent event) {
		ifPresent(event, record -> {
			moveCursor(record, event);
			record.setStatus(Status.PAUSE);
			testingRecordMapper.save(record);
		});
	}

	@Override
	@Transactional
	public void completed(TestingEvent event) {
		ifPresent(event, record -> {
			record.setCurrentIteration(0);
			record.setThreadGroupCursor(0);
			record.setEndTime(new Date());
			record.setStatus(Status.COMPLETED);
			testingRecordMapper.save(record);
		});
	}

	@Override
	@Transactional
	public void metric(TestingEvent event, int doneRequests) {
		log.info("testing {} done requests {}/{}", event.getName(), doneRequests,
			getActiveThreads(event) * event.getRequestsPerThread());
	}

	@Override
	@Transactional
	public void failed(TestingEvent event, Throwable e) {
		ifPresent(event, record -> {
			moveCursor(record, event);
			record.setStatus(Status.FAILURE);
			record.setRemark(e.getMessage());
			testingRecordMapper.save(record);
		});
	}

	@Override
	@Transactional
	public void beforeEachExecute(TestingEvent event) {
		ifPresent(event, record -> {
			moveCursor(record, event);
			testingRecordMapper.save(record);
		});
	}

	@Override
	@Transactional
	public void afterEachExecute(TestingEvent event, Result result) {
		ifPresent(event, record -> {
			SummaryReport summaryReport = SummaryReporterConverter.INSTANCE
				.convert(result.getResponse());
			summaryReport.setTestingRecordId(record.getId());
			summaryReport.setStartTime(result.getStartTime());
			summaryReport.setEndTime(result.getEndTime());
			summaryReportMapper.save(summaryReport);
		});
	}

	private void ifPresent(TestingEvent event, Consumer<TestingRecord> consumer) {
		testingRecordMapper.select(event.getId()).ifPresent(consumer);
	}

	private void moveCursor(TestingRecord record, TestingEvent event) {
		record.setThreadGroupCursor(event.getThreadGroupCursor());
		record.setActiveThreads(getActiveThreads(event));
		record.setCurrentIteration(event.getIteration());
	}

	private int getActiveThreads(TestingEvent event) {
		int index = event.getThreadGroupCursor();
		List<Integer> groups = event.getThreadGroups();
		if (index < groups.size()) {
			return groups.get(index);
		}
		return 0;
	}
}
