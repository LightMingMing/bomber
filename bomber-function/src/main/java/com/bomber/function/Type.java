package com.bomber.function;

/**
 * 类型
 *
 * @author MingMing Zhao
 */
public enum Type {

	/**
	 * 基础类型
	 */
	BASE(0),

	/**
	 * 日期
	 */
	DATE(1),

	/**
	 * 脚本
	 */
	SCRIPT(2),

	/**
	 * SQL
	 */
	SQL(3),

	/**
	 * 其它
	 */
	OTHER(Integer.MAX_VALUE);

	private final int order;

	Type(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
}
