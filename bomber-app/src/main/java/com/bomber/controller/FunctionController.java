package com.bomber.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.service.FunctionExecutorService;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping(FunctionController.API)
public class FunctionController {

	public static final String API = "/api/function";

	private final FunctionExecutorService functionExecutor;

	public FunctionController(FunctionExecutorService functionExecutor) {
		this.functionExecutor = functionExecutor;
	}

	@GetMapping(value = "/{id}", params = "limit")
	public List<Map<String, String>> generateBatch(@PathVariable Integer id, @RequestParam int offset,
												   @RequestParam int limit,
												   @RequestParam(required = false) Set<String> columns) {
		return functionExecutor.execute(id, offset, limit, columns);
	}

	@GetMapping(value = "/{id}")
	public Map<String, String> generateOne(@PathVariable Integer id, @RequestParam int offset) {
		return functionExecutor.execute(id, offset);
	}
}
