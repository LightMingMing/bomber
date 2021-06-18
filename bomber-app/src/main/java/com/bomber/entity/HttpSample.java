package com.bomber.entity;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

/**
 * Http 请求脚本
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class HttpSample extends BaseEntity<Integer> {
	private static final long serialVersionUID = -1009039514627961069L;

	/**
	 * 所属工作空间 ID
	 */
	private Integer workspaceId;

	/**
	 * 序号, 同一个工作空间的 Http 请求脚本顺序
	 */
	private Integer orderNumber;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * Http 方法
	 */
	private HttpMethod method;

	/**
	 * url
	 */
	private String url;

	/**
	 * 请求头
	 */
	private List<HttpHeader> headers;

	/**
	 * 请求体
	 */
	private String body;

	/**
	 * 断言
	 */
	private List<Assertion> assertions;

	/**
	 * 是否启用
	 */
	private boolean enabled;

	/**
	 * 创建日期
	 */
	private Date createDate;

	/**
	 * 修改日期
	 */
	private Date modifyDate;
}
