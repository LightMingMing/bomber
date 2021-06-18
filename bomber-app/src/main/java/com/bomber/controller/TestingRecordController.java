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

import com.bomber.entity.TestingRecord;
import com.bomber.mapper.TestingRecordMapper;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping("/api/testingRecords")
public class TestingRecordController implements CrudController<Long, TestingRecord> {

	private final TestingRecordMapper testingRecordMapper;

	public TestingRecordController(TestingRecordMapper testingRecordMapper) {
		this.testingRecordMapper = testingRecordMapper;
	}

	@Override
	@PostMapping
	public int create(@RequestBody TestingRecord testingRecord) {
		return testingRecordMapper.create(testingRecord);
	}

	@Override
	@DeleteMapping("/{id}")
	public int delete(@PathVariable Long id) {
		return testingRecordMapper.delete(id);
	}

	@Override
	public int update(@RequestBody TestingRecord testingRecord) {
		// NOOP
		return 0;
	}

	@Override
	@GetMapping("/{id}")
	public Optional<TestingRecord> select(@PathVariable Long id) {
		return testingRecordMapper.select(id);
	}

	@GetMapping
	public Page<TestingRecord> paging(@RequestParam int page, @RequestParam int size) {
		return testingRecordMapper.paging(PageRequest.of(page, size));
	}

	@GetMapping(params = "httpSampleId")
	public List<TestingRecord> listByHttpSample(@RequestParam Integer httpSampleId) {
		return testingRecordMapper.findAllByHttpSample(httpSampleId);
	}
}
