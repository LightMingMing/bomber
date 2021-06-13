package com.bomber.service;

import java.util.List;

import com.bomber.model.SummaryReport;

public interface SummaryReportService {

	List<SummaryReport> list(Long recordId);

}
