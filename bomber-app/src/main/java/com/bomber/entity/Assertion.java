package com.bomber.entity;

import com.bomber.asserter.Condition;
import lombok.Getter;
import lombok.Setter;

/**
 * 断言
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class Assertion {

	/**
	 * 断言器
	 */
	private String asserter;

	/**
	 * 表达式, 获取真实值
	 */
	private String expression;

	/**
	 * 匹配条件
	 */
	private Condition condition;

	/**
	 * 期望值
	 */
	private String expected;
}
