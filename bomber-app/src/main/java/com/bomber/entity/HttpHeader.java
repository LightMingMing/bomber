package com.bomber.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Http 请求头
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class HttpHeader {

	private String name;

	private String value;
}
