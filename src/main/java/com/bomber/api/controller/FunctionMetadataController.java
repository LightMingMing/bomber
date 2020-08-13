package com.bomber.api.controller;

import com.bomber.functions.FunctionMetadata;
import com.bomber.functions.FunctionMetadataHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/functionMetadata")
public class FunctionMetadataController {

	@GetMapping("/{name}")
	public FunctionMetadata get(@PathVariable String name) {
		FunctionMetadata metadata = FunctionMetadataHelper.getFunctionMetadata(name);
		if (metadata == null) {
			throw new IllegalArgumentException("function '" + name + "' does not exist");
		}
		return metadata;
	}
}
