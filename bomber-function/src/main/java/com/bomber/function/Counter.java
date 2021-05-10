package com.bomber.function;

/**
 * 计数器
 *
 * @author MingMing Zhao
 */
@FuncInfo
public class Counter implements Producer<String> {

	private int counter = 0;

	@Override
	public String execute() {
		return (counter++) + "";
	}

	@Override
	public void jump(int steps) {
		this.counter += steps;
	}

}
