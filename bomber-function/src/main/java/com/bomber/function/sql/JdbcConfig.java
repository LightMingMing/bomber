package com.bomber.function.sql;

import java.util.Objects;

public class JdbcConfig {

	private final String url;

	private final String user;

	private final String password;

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

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
