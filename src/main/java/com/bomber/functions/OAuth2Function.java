package com.bomber.functions;

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
import org.springframework.web.client.HttpClientErrorException;

public class OAuth2Function extends AbstractFunction {

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

	@Override
	public String getRequiredArgs() {
		return "accessTokenEndpoint, grant_type, client_id, client_secret";
	}

	@Override
	public String getOptionalArgs() {
		return "username, password, device_id, device_name";
	}

	@Override
	protected void doInit(Map<String, String> params) {
		this.accessTokenEndpoint = params.get("accessTokenEndpoint");
		this.grantType = params.get("grant_type");
		this.clientId = params.get("client_id");
		this.clientSecret = params.get("client_secret");
		this.username = params.get("username");
		this.password = params.get("password");
		this.deviceId = params.get("device_id");
		this.deviceName = params.get("device_name");
	}

	@Override
	public String execute() {
		if (cacheManager == null) {
			ApplicationContext context = ApplicationContextUtils.getApplicationContext();
			if (context == null) {
				throw new IllegalStateException("OAuth2 function should be running in a web application");
			}
			this.cacheManager = context.getBean(CacheManager.class);
		}

		Token token = (Token) cacheManager.get(getKey(), namespace);
		if (token == null || token.isExpired()) {
			token = requestToken();
			cacheManager.put(getKey(), token, timeToLive(token), TimeUnit.SECONDS, namespace);
		}
		// TODO refresh token ?
		return token.getAccessToken();
	}

	private String getKey() {
		return clientId;
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
			throw new RuntimeException(e);
		}
	}
}
