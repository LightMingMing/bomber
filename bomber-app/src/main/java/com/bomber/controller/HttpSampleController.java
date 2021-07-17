package com.bomber.controller;

import java.util.List;
import java.util.Optional;

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

	public HttpSampleController(HttpSampleMapper httpSampleMapper) {
		this.httpSampleMapper = httpSampleMapper;
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
}
