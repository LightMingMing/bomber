package com.bomber.engine;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.engine.model.Result;
import com.bomber.engine.monitor.TestingEvent;
import com.bomber.engine.monitor.TestingListener;
import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.BombingStatus;
import com.bomber.model.SummaryReport;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试监听
 *
 * @author MingMing Zhao
 */
@Slf4j
public class BomberTestingListener implements TestingListener {

	@Autowired
	private BombingRecordManager bombingRecordManager;

	@Autowired
	private SummaryReportManager summaryReportManager;

	@Override
	@Transactional
	public boolean started(TestingEvent event) {
		BombingRecord record = getRecord(event);
		if (record == null) {
			return false;
		}
		if (record.getStartTime() == null) {
			record.setStartTime(new Date());
		}
		record.setStatus(BombingStatus.RUNNING);
		bombingRecordManager.save(record);
		return true;
	}

	@Override
	@Transactional
	public void paused(TestingEvent event) {
		BombingRecord record = getRecord(event);
		if (record == null) {
			return;
		}
		moveCursor(record, event);
		record.setStatus(BombingStatus.PAUSE);
		bombingRecordManager.save(record);
	}

	@Override
	@Transactional
	public void completed(TestingEvent event) {
		BombingRecord record = getRecord(event);
		if (record == null) {
			return;
		}
		record.setCurrentIterations(0);
		record.setThreadGroupCursor(0);
		record.setEndTime(new Date());
		record.setStatus(BombingStatus.COMPLETED);
		bombingRecordManager.save(record);
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
		BombingRecord record = getRecord(event);
		if (record == null) {
			return;
		}
		moveCursor(record, event);
		record.setStatus(BombingStatus.FAILURE);
		record.setRemark(e.getMessage());
		bombingRecordManager.save(record);
	}

	@Override
	@Transactional
	public void beforeEachExecute(TestingEvent event) {
		BombingRecord record = getRecord(event);
		if (record == null) {
			return;
		}
		moveCursor(record, event);
		bombingRecordManager.save(record);
	}

	@Override
	@Transactional
	public void afterEachExecute(TestingEvent event, Result result) {
		BombingRecord record = getRecord(event);
		if (record == null) {
			return;
		}
		SummaryReport summaryReport = SummaryReporterConverter.INSTANCE
				.convertToSummaryReport(result.getResponse());
		summaryReport.setBombingRecord(record);
		summaryReport.setStartTime(result.getStartTime());
		summaryReport.setEndTime(result.getEndTime());
		summaryReportManager.save(summaryReport);
	}

	private BombingRecord getRecord(TestingEvent event) {
		return bombingRecordManager.get(event.getId());
	}

	private void moveCursor(BombingRecord record, TestingEvent event) {
		record.setThreadGroupCursor(event.getThreadGroupCursor());
		record.setActiveThreads(getActiveThreads(event));
		record.setCurrentIterations(event.getIteration());
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
