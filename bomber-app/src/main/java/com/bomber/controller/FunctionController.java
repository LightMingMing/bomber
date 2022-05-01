package com.bomber.controller;

import com.bomber.service.FunctionExecutorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
	public List<Map<String, String>> execute(@PathVariable Integer id, @RequestParam int offset,
											 @RequestParam int limit,
											 @RequestParam(required = false) Set<String> columns) {
		return functionExecutor.execute(id, offset, limit, columns);
	}

	@GetMapping(value = "/{id}", params = "!limit")
	public Map<String, String> execute(@PathVariable Integer id, @RequestParam int offset) {
		return functionExecutor.execute(id, offset);
	}

	@GetMapping(value = "/{id}", params = "limit", headers = "Content-Type=text/plain")
	public String getCommaSeparatedResult(@PathVariable Integer id, @RequestParam int offset,
										  @RequestParam int limit,
										  @RequestParam List<String> columns) {
		List<Map<String, String>> result = functionExecutor.execute(id, offset, limit, columns);
		StringJoiner joiner = new StringJoiner("\n");
		for (Map<String, String> map : result) {
			joiner.add(columns.stream().map(map::get).collect(Collectors.joining(",")));
		}
		return joiner.toString();
	}
}
