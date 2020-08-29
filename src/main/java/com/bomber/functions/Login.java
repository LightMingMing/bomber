package com.bomber.functions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;
import org.ironrhino.core.cache.CacheManager;
import org.ironrhino.core.spring.http.client.RestTemplate;
import org.ironrhino.core.util.ApplicationContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.bomber.functions.core.FuncInfo;

@FuncInfo(requiredArgs = "url, form", optionalArgs = "expiresIn")
public class Login extends StringFunction {

	private static final RestTemplate template = new RestTemplate();

	private static final String namespace = "F_Login";

	private CacheManager cacheManager;

	private String url;

	private String form;

	private int expiresIn;

	public static String handleCookie(List<String> cookies) {
		Map<String, String> pairs = new LinkedHashMap<>(cookies.size());
		for (String cookie : cookies) {
			// eg: U=admin; Max-Age=31536000; Expires=Thu, 19-Aug-2021 02:02:53 GMT; Path=/
			// remove cookie attributes
			String[] arr = cookie.split(";", 2);
			if (arr.length == 0) {
				continue;
			}
			String[] pair = arr[0].split("=", 2);
			// remove duplicate
			pairs.put(pair[0], pair[1]);
		}
		StringJoiner joiner = new StringJoiner(";");
		pairs.forEach((k, v) -> joiner.add(k + "=" + v));
		return joiner.toString();
	}

	@Override
	public void init(Input input) {
		ApplicationContext context = ApplicationContextUtils.getApplicationContext();
		if (context == null) {
			throw new IllegalStateException("Login function should be running in a web application");
		}
		this.cacheManager = context.getBean(CacheManager.class);
		this.expiresIn = Integer.parseInt(input.getOrDefault("expiresIn", "600"));
	}

	@Override
	public String execute(Input input) {
		this.url = input.get("url");
		this.form = input.get("form");

		String cookie = (String) cacheManager.get(getKey(), namespace);
		if (cookie == null) {
			cookie = requestSetCookie();
			cacheManager.put(getKey(), cookie, expiresIn, TimeUnit.SECONDS, namespace);
		}
		return cookie;
	}

	private String getKey() {
		return url + "_" + form;
	}

	private String requestSetCookie() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<String> request = new HttpEntity<>(form, headers);
		try {
			ResponseEntity<byte[]> response = template.postForEntity(url, request, byte[].class);
			List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
			if (cookies == null) {
				throw new RuntimeException("Can't found " + HttpHeaders.SET_COOKIE);
			}
			return handleCookie(cookies);
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}
	}

}
