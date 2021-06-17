package com.bomber.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 函数配置
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class FunctionConfigure extends BaseEntity<Integer> {

	private static final long serialVersionUID = -421505359173493404L;

	/**
	 * 所属工作空间 ID
	 */
	private Integer workspaceId;

	/**
	 * 序号, 同一个工作空间的函数配置顺序
	 */
	private Integer orderNumber;

	/**
	 * 名称, 通常做为函数运行结果的变量名
	 */
	private String name;

	/**
	 * 函数名
	 */
	private String functionName;

	/**
	 * 参数值
	 */
	private List<ArgumentValue> argumentValues;

	/**
	 * 是否启用
	 */
	private boolean enabled;
}
