package com.bomber.functions;

public class FunctionTest {

	public static String execute(Function func, int count) {
		String result = null;

		for (int i = 0; i < count; i++) {
			result = func.execute();
		}

		return result;
	}
}