package com.bomber.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bomber.engine.monitor.TestingListener;

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

	@Bean
	public TestingListener listener() {
		return new BomberTestingListener();
	}

}
