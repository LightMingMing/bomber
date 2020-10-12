package com.bomber.sql;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JdbcConfig {

	private String url;

	private String user;

	private String password;

	public JdbcConfig(String url, String user, String password) {
		this.url = Objects.requireNonNull(url, "url");
		this.user = Objects.requireNonNull(user, "user");
		this.password = Objects.requireNonNull(password, "password");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JdbcConfig that = (JdbcConfig) o;
		return Objects.equals(url, that.url) && Objects.equals(user, that.user)
				&& Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, user, password);
	}
}
