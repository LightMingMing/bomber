package com.bomber.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.functions.Function;
import com.bomber.manager.PayloadManager;
import com.bomber.model.Payload;
import com.bomber.model.PayloadOption;

@RestController
@RequestMapping("/payload")
public class PayloadController {

	private final PayloadManager payloadManager;

	public PayloadController(PayloadManager payloadManager) {
		this.payloadManager = payloadManager;
	}

	@GetMapping("/{id}")
	public Result list(@PathVariable String id, @RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "100") int limit, @RequestParam(required = false) List<String> columns) {
		Payload payload = payloadManager.get(id);
		if (payload == null) {
			throw new IllegalArgumentException("payload does not exist");
		}
		List<PayloadOption> options = payload.getOptions();
		List<Function> functions = new ArrayList<>();
		if (columns == null) {
			columns = new ArrayList<>();
			for (PayloadOption option : options) {
				Function func = option.createQuietly();
				func.skip(offset);
				functions.add(func);
				columns.add(option.getKey());
			}
		} else {
			for (String column : columns) {
				Function func = null;
				for (PayloadOption option : options) {
					if (option.getKey().equals(column)) {
						func = option.createQuietly();
						func.skip(offset);
					}
				}
				if (func == null) {
					throw new IllegalArgumentException("invalid column '" + column + "'");
				}
				functions.add(func);
			}
		}

		List<String> data = new ArrayList<>(limit);
		for (int i = 0; i < limit; i++) {
			data.add(functions.stream().map(Function::execute).collect(Collectors.joining(",")));
		}

		return new Result(columns, data);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private static class Result {
		private List<String> columns;
		private List<String> data;
	}
}
