package com.bomber.engine.rpc;

import java.net.URI;
import java.util.function.Consumer;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.bomber.common.util.JsonUtils;
import reactor.core.publisher.Mono;

/**
 * 基于 WebSocket 的 Bombardier 客户端
 *
 * @author MingMing Zhao
 */
public class BombardierWebSocketClient implements BombardierClient {

	private final WebSocketClient client;

	public BombardierWebSocketClient(WebSocketClient client) {
		this.client = client;
	}

	public Mono<Void> execute(URI uri, BombardierRequest request, Consumer<WebSocketMessage> callback) {
		return client.execute(uri, session -> session
				.send(publish(session, request))
				.thenMany(session.receive().doOnNext(callback).then())
				.then());
	}

	// publish message
	public Mono<WebSocketMessage> publish(WebSocketSession session, BombardierRequest request) {
		return Mono.just(session.textMessage(JsonUtils.toJson(request)));
	}

}
