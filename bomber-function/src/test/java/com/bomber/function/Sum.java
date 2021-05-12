package com.bomber.function;

@FuncInfo(requiredArgs = "a, b")
public class Sum implements Function {

	public String execute(String a, String b) {
		return a + b + "";
	}
}
