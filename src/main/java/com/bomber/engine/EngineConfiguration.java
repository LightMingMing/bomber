package com.bomber.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 引擎配置
 *
 * @author MingMing Zhao
 */
@Configuration(proxyBeanMethods = false)
public class EngineConfiguration {

	@Bean
	public BomberContextRegistry contextRegistry() {
		return new BomberContextRegistryImpl();
	}
}
