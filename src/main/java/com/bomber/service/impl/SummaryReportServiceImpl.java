package com.bomber.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.manager.SummaryReportManager;
import com.bomber.model.SummaryReport;
import com.bomber.service.SummaryReportService;

@Service
public class SummaryReportServiceImpl implements SummaryReportService {

	private final SummaryReportManager summaryReportManager;

	public SummaryReportServiceImpl(SummaryReportManager summaryReportManager) {
		this.summaryReportManager = summaryReportManager;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SummaryReport> list(String recordId) {
		return summaryReportManager.list(recordId);
	}
}
