package com.bomber.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.bomber.engine.monitor.TestingListener;

/**
 * 引擎配置
 *
 * @author MingMing Zhao
 */
@Configuration(proxyBeanMethods = false)
public class WebSocketBasedEngineConfiguration {

	@Bean
	public WebSocketClientFactory webSocketClientFactory() {
		return new WebSocketClientFactory();
	}

	@Bean
	@Primary
	public BomberEngine bomberEngine(BomberContextRegistry registry,
									 WebSocketClient client,
									 TestingListener... listeners) {
		return new WebSocketBasedBomberEngine(registry, client, listeners);
	}
}
