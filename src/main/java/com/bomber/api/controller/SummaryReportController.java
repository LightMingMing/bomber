package com.bomber.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.api.vo.SummaryReportVo;
import com.bomber.model.SummaryReport;
import com.bomber.service.SummaryReportService;

@RestController
@RequestMapping("/summaryReport")
public class SummaryReportController {

	protected final static Comparator<SummaryReport> comparator = Comparator
			.comparingInt(SummaryReport::getNumberOfThreads).thenComparing(SummaryReport::getStartTime);

	private final SummaryReportService bombingRecordService;

	public SummaryReportController(SummaryReportService bombingRecordService) {
		this.bombingRecordService = bombingRecordService;
	}

	@GetMapping("/list")
	public List<SummaryReportVo> list(@RequestParam("recordId") String recordId) {
		return bombingRecordService.list(recordId).stream().sorted(comparator).map(this::map)
				.collect(Collectors.toList());
	}

	protected SummaryReportVo map(SummaryReport report) {
		SummaryReportVo reportVo = new SummaryReportVo();
		reportVo.setThreads(report.getNumberOfThreads());
		reportVo.setTps(report.getTps());
		reportVo.setAvg(report.getAvg());
		reportVo.setMin(report.getMin());
		reportVo.setP25(report.getPoint25());
		reportVo.setP50(report.getPoint50());
		reportVo.setP75(report.getPoint75());
		reportVo.setP90(report.getPoint90());
		reportVo.setP95(report.getPoint95());
		reportVo.setP99(report.getPoint99());
		reportVo.setMax(report.getMax());
		return reportVo;
	}
}
