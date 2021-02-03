package com.bomber.manager;

import java.util.stream.Stream;

import org.ironrhino.core.service.BaseManager;

import com.bomber.model.SummaryReport;
import com.bomber.vo.SummaryReportVo;

public interface SummaryReportManager extends BaseManager<SummaryReport> {

	Stream<SummaryReportVo> list(String recordId);

}
