package com.bomber.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.manager.SummaryReportManager;
import com.bomber.vo.SummaryReportVo;

@RestController
@RequestMapping("/summaryReports")
public class SummaryReportController {

	protected final static Comparator<SummaryReportVo> comparator = Comparator.comparingInt(SummaryReportVo::getThreads)
			.thenComparing(SummaryReportVo::getStartTime);

	private final SummaryReportManager summaryReportManager;

	public SummaryReportController(SummaryReportManager summaryReportManager) {
		this.summaryReportManager = summaryReportManager;
	}

	@GetMapping
	public List<SummaryReportVo> list(@RequestParam("recordId") String recordId) {
		return summaryReportManager.list(recordId).sorted(comparator).collect(Collectors.toList());
	}
}
