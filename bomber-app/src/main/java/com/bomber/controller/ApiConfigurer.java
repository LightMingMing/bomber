package com.bomber.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Api Configurer
 *
 * @author MingMing Zhao
 */
@Configuration(proxyBeanMethods = false)
public class ApiConfigurer implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
	}

}
