package com.bomber.controller;

import com.bomber.function.Function;
import com.bomber.function.model.FunctionMetadata;
import com.bomber.function.util.FunctionHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/functionMetadata")
public class FunctionMetadataController {

	@GetMapping("/{name}")
	public FunctionMetadataVO get(@PathVariable String name) {
		FunctionMetadata metadata = FunctionHelper.getFunctionMetadata(name);
		if (metadata == null) {
			throw new IllegalArgumentException("function '" + name + "' does not exist");
		}
		return convert(metadata);
	}

	@Getter
	@Setter
	static class FunctionMetadataVO {
		private String name;

		private Class<Function> functionType;

		private String requiredArgs;

		private String optionalArgs;

		private String customArg;

		private boolean retAllArgs = false;

		private String retArg;

		private boolean parallel;
	}

	static FunctionMetadataVO convert(FunctionMetadata functionMetadata) {
		FunctionMetadataVO fm = new FunctionMetadataVO();
		fm.setName(functionMetadata.getName());
		fm.setFunctionType(functionMetadata.getFunctionType());
		fm.setRequiredArgs(functionMetadata.getRequiredArgs());
		fm.setOptionalArgs(functionMetadata.getOptionalArgs());
		fm.setCustomArg(functionMetadata.getCustomArg());
		fm.setRetAllArgs(functionMetadata.isRetAllArgs());
		fm.setRetArg(functionMetadata.getRetArg());
		fm.setParallel(functionMetadata.isParallel());
		return fm;
	}
}