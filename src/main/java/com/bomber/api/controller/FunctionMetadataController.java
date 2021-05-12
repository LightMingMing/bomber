package com.bomber.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.function.model.FunctionMetadata;
import com.bomber.function.util.FunctionHelper;

@RestController
@RequestMapping("/functionMetadata")
public class FunctionMetadataController {

	@GetMapping("/{name}")
	public FunctionMetadata get(@PathVariable String name) {
		FunctionMetadata metadata = FunctionHelper.getFunctionMetadata(name);
		if (metadata == null) {
			throw new IllegalArgumentException("function '" + name + "' does not exist");
		}
		return metadata;
	}
}
