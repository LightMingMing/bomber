package com.bomber.engine;

import java.time.Duration;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import io.netty.channel.ChannelOption;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/**
 * WebSocketClient 工厂
 *
 * @author MingMing Zhao
 */
public class WebSocketClientFactory implements FactoryBean<WebSocketClient>, DisposableBean {

	private final ConnectionProvider connectionProvider = ConnectionProvider
			.builder("bombardier-client")
			.maxConnections(1)
			.maxIdleTime(Duration.ofMinutes(30))
			.maxLifeTime(Duration.ofHours(1))
			.pendingAcquireTimeout(Duration.ofMinutes(1))
			.build();

	private final LoopResources loopResources = LoopResources
			.create("bombardier-client-loop", 1, false);

	@Nullable
	@Override
	public WebSocketClient getObject() throws Exception {
		HttpClient client = HttpClient
				.create(connectionProvider)
				.runOn(loopResources)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.responseTimeout(Duration.ofMinutes(1));
		return new ReactorNettyWebSocketClient(client);
	}

	@Nullable
	@Override
	public Class<?> getObjectType() {
		return WebSocketClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		Mono.when(loopResources.disposeLater(), connectionProvider.disposeLater())
				.block(Duration.ofSeconds(5));
	}
}
