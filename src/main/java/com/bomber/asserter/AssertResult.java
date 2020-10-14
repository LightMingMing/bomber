package com.bomber.asserter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssertResult {

	private static AssertResult SUCCESS = new AssertResult(true);

	private boolean successful;

	private String error;

	private AssertResult(boolean successful) {
		this.successful = successful;
	}

	private AssertResult(String error) {
		this.successful = false;
		this.error = error;
	}

	public static AssertResult success() {
		return SUCCESS;
	}

	public static AssertResult error(String error) {
		return new AssertResult(error);
	}
}
