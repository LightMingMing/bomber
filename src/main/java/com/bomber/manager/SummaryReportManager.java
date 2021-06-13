package com.bomber.manager;

import java.util.List;

import org.ironrhino.core.service.BaseManager;

import com.bomber.model.SummaryReport;

public interface SummaryReportManager extends BaseManager<SummaryReport> {

	List<SummaryReport> list(Long recordId);

}
