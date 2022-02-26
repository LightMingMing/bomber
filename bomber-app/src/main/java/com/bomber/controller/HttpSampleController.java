package com.bomber.controller;

import java.util.List;
import java.util.Optional;

import com.bomber.service.HttpSampleResult;
import com.bomber.service.HttpSampleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.entity.HttpSample;
import com.bomber.mapper.HttpSampleMapper;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping("/api/httpSamples")
public class HttpSampleController implements CrudController<Integer, HttpSample> {

	private final HttpSampleMapper httpSampleMapper;

	private final HttpSampleService httpSampleService;

	public HttpSampleController(HttpSampleMapper httpSampleMapper, HttpSampleService httpSampleService) {
		this.httpSampleMapper = httpSampleMapper;
		this.httpSampleService = httpSampleService;
	}

	@Override
	@PostMapping
	public int create(@RequestBody HttpSample httpSample) {
		return httpSampleMapper.create(httpSample);
	}

	@Override
	@DeleteMapping("/{id}")
	public int delete(@PathVariable Integer id) {
		return httpSampleMapper.delete(id);
	}

	@Override
	@PutMapping
	public int update(@RequestBody HttpSample httpSample) {
		return httpSampleMapper.update(httpSample);
	}

	@Override
	@GetMapping("/{id}")
	public Optional<HttpSample> select(@PathVariable Integer id) {
		return httpSampleMapper.select(id);
	}

	@GetMapping
	public List<HttpSample> findAllByGroup(@RequestParam Integer groupId) {
		return httpSampleMapper.findAllByGroup(groupId);
	}

	@PutMapping("/reorder")
	public int reorder(@RequestBody List<Integer> ids) {
		return httpSampleMapper.reorder(ids);
	}

	@GetMapping("/preview")
	public String preview(int id, int index) {
		return httpSampleService.renderRequest(id, index);
	}

	@GetMapping(value = "/execute")
	public HttpSampleResult execute(int id) {
		return httpSampleService.execute(id);
	}

	@GetMapping(value = "/execute", params = "index")
	public HttpSampleResult execute(int id, int index) {
		return httpSampleService.execute(id, index);
	}

	@GetMapping(value = "/execute", params = {"index", "size"})
	public List<HttpSampleResult> execute(int id, int index, int size) {
		return httpSampleService.executeBatch(id, index, size);
	}
}
