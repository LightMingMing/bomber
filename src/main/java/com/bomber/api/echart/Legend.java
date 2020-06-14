package com.bomber.api.echart;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Legend {

	@Getter
	private final List<String> data;

	public Legend(String... legends) {
		data = new ArrayList<>();
		data.addAll(Arrays.asList(legends));
	}

}
