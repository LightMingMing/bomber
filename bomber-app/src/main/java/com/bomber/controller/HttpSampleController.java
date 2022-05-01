package com.bomber.controller;

import com.bomber.entity.HttpSample;
import com.bomber.mapper.HttpSampleMapper;
import com.bomber.service.HttpSampleResult;
import com.bomber.service.HttpSampleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
	public String preview(@RequestParam int id, @RequestParam int index) {
		return httpSampleService.renderRequest(id, index);
	}

	@GetMapping(value = "/execute")
	public HttpSampleResult execute(@RequestParam int id) {
		return httpSampleService.execute(id);
	}

	@GetMapping(value = "/execute", params = "index")
	public HttpSampleResult execute(@RequestParam int id, @RequestParam int index) {
		return httpSampleService.execute(id, index);
	}

	@GetMapping(value = "/execute", params = {"index", "size"})
	public List<HttpSampleResult> execute(@RequestParam int id, @RequestParam int index, @RequestParam int size) {
		return httpSampleService.executeBatch(id, index, size);
	}
}
