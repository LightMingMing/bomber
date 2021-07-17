package com.bomber.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.entity.SummaryReport;
import com.bomber.mapper.SummaryReportMapper;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping("/api/summaryReports")
public class SummaryReportController implements CrudController<Long, SummaryReport> {

	private final SummaryReportMapper summaryReportMapper;

	public SummaryReportController(SummaryReportMapper summaryReportMapper) {
		this.summaryReportMapper = summaryReportMapper;
	}

	@Override
	@PostMapping
	public int create(@RequestBody SummaryReport summaryReport) {
		return summaryReportMapper.create(summaryReport);
	}

	@Override
	@DeleteMapping("/{id}")
	public int delete(@PathVariable Long id) {
		return summaryReportMapper.delete(id);
	}

	@Override
	public int update(@RequestBody SummaryReport summaryReport) {
		// NOOP
		return 0;
	}

	@Override
	@GetMapping("/{id}")
	public Optional<SummaryReport> select(@PathVariable Long id) {
		return summaryReportMapper.select(id);
	}

	@GetMapping
	public Page<SummaryReport> paging(@RequestParam int page, @RequestParam int size) {
		return summaryReportMapper.paging(PageRequest.of(page, size));
	}

	@GetMapping(params = "testingRecordId")
	public List<SummaryReport> listByHttpSample(@RequestParam Long testingRecordId) {
		return summaryReportMapper.findAllByTestingRecord(testingRecordId);
	}
}
