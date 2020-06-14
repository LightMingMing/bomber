package com.bomber.model;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class HttpHeader implements Serializable {

	private static final long serialVersionUID = -7646545576210776876L;

	private String name;

	private String value;

	public HttpHeader() {
	}

	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}
}
