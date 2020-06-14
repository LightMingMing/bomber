package com.bomber.api.echart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Title {

	private String text;

	private String subText;

	public Title(String text) {
		this(text, null);
	}

	public Title(String text, String subText) {
		this.text = text;
		this.subText = subText;
	}
}
