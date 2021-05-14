package com.bomber.engine.model;

import java.util.List;

import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.Setter;

/**
 * 有效载荷
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class Payload {

	/**
	 * 有效载荷 URL
	 */
	@NonNull
	private String url;

	/**
	 * 起始索引
	 */
	private int start;

	/**
	 * 作用域
	 */
	@NonNull
	private Scope scope;

	/**
	 * 函数上下文
	 */
	@NonNull
	private List<FunctionInfo> functionInfos;

}
