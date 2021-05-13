package com.bomber.engine.model;

/**
 * 变量作用域
 *
 * @author MingMing Zhao
 */
public enum Scope {
	/**
	 * 全局
	 */
	Benchmark,
	/**
	 * 线程组
	 */
	Group,
	/**
	 * 线程
	 */
	Thread,
	/**
	 * 请求
	 */
	Request
}
