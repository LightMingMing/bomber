package com.bomber.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.bomber.engine.monitor.TestingListener;

/**
 * @author MingMing Zhao
 */
@Configuration(proxyBeanMethods = false)
public class WebSocketBasedEngineConfiguration {

	@Bean
	public WebSocketClientFactory webSocketClientFactory() {
		return new WebSocketClientFactory();
	}

	@Bean
	public BomberContextRegistry contextRegistry() {
		return new BomberContextRegistryImpl();
	}

	@Bean
	public BomberEngine bomberEngine(BomberContextRegistry registry,
									 WebSocketClient client,
									 TestingListener... listeners) {
		return new WebSocketBasedBomberEngine(registry, client, listeners);
	}
}
