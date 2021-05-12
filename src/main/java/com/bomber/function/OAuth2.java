package com.bomber.function;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.ironrhino.core.cache.CacheManager;
import org.ironrhino.core.spring.http.client.RestTemplate;
import org.ironrhino.core.util.ApplicationContextUtils;
import org.ironrhino.rest.client.token.DefaultToken;
import org.ironrhino.rest.client.token.Token;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

@FuncInfo(requiredArgs = "accessTokenEndpoint, grant_type, client_id, client_secret", optionalArgs = "username, password, device_id, device_name")
public class OAuth2 implements Function {

	private static final RestTemplate template = new RestTemplate();

	private static final String namespace = "F_OAuth2";

	private CacheManager cacheManager;

	private String accessTokenEndpoint;

	private String grantType;

	private String clientId;

	private String clientSecret;

	private String username;

	private String password;

	private String deviceId;

	private String deviceName;

	protected CacheManager getCacheManager() {
		if (this.cacheManager != null) {
			return this.cacheManager;
		}
		ApplicationContext context = ApplicationContextUtils.getApplicationContext();
		if (context == null) {
			throw new IllegalStateException("OAuth2 function should be running in a web application");
		}
		return this.cacheManager = context.getBean(CacheManager.class);
	}

	private void reset(Map<String, String> input) {
		this.accessTokenEndpoint = input.get("accessTokenEndpoint");
		this.grantType = input.get("grant_type");
		this.clientId = input.get("client_id");
		this.clientSecret = input.get("client_secret");
		this.username = input.get("username");
		this.password = input.get("password");
		this.deviceId = input.get("device_id");
		this.deviceName = input.get("device_name");
	}

	public String execute(Map<String, String> input) {
		reset(input);
		Token token = (Token) getCacheManager().get(getKey(), namespace);
		if (token == null || token.isExpired()) {
			token = requestToken();
			getCacheManager().put(getKey(), token, timeToLive(token), TimeUnit.SECONDS, namespace);
		}
		// TODO refresh token ?
		return token.getAccessToken();
	}

	private String getKey() {
		return StringUtils.hasLength(username) ? username : clientId;
	}

	private int timeToLive(Token token) {
		int expiresIn = token.getExpiresIn();
		int offset = expiresIn > 3600 ? expiresIn / 20 : 300;
		expiresIn -= offset;
		return expiresIn;
	}

	private Token requestToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", grantType);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("username", username);
		params.add("password", password);
		params.add("device_id", deviceId);
		params.add("device_name", deviceName);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		try {
			return template.postForEntity(accessTokenEndpoint, request, DefaultToken.class).getBody();
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("OAuth2 failed to access '" + accessTokenEndpoint + "'", e);
		}
	}
}
