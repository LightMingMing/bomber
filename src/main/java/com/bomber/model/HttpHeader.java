package com.bomber.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

import org.ironrhino.core.metadata.UiConfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HttpHeader implements Serializable {

	private static final long serialVersionUID = -7646545576210776876L;

	@UiConfig(cssClass = "header-name")
	private String name;

	@UiConfig(cssClass = "header-value")
	private String value;

	public HttpHeader() {
	}

	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		HttpHeader header = (HttpHeader) o;
		return Objects.equals(name, header.name) && Objects.equals(value, header.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}
}
