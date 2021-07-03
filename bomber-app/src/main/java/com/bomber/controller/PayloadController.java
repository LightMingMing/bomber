package com.bomber.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.function.FunctionGenerator;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping(PayloadController.API_PAYLOAD)
public class PayloadController {

	public static final String API_PAYLOAD = "/api/payload";

	private final FunctionGenerator functionGenerator;

	public PayloadController(FunctionGenerator functionGenerator) {
		this.functionGenerator = functionGenerator;
	}

	@GetMapping("/{id}")
	public List<Map<String, String>> generateBatch(@PathVariable Integer id, @RequestParam int offset,
												   @RequestParam int limit,
												   @RequestParam Set<String> columns) {
		return functionGenerator.generateBatch(id, offset, limit, columns);
	}

	@GetMapping(value = "/{id}", params = "!limit")
	public Map<String, String> generateOne(@PathVariable Integer id, @RequestParam int offset) {
		return functionGenerator.generateOne(id, offset);
	}
}
