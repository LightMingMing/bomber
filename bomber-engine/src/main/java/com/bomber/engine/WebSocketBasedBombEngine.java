package com.bomber.engine;

import java.net.URI;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.bomber.engine.model.BomberContext;
import com.bomber.engine.model.Result;
import com.bomber.engine.rpc.BombardierRequest;
import com.bomber.engine.rpc.BombardierResponse;
import com.bomber.engine.rpc.BombardierWebSocketClient;
import com.bomber.engine.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 WebSocket 的引擎实现
 */
@Slf4j
public class WebSocketBasedBombEngine extends SingleThreadBomberEngine implements InitializingBean, DisposableBean {

	private final BombardierWebSocketClient client;

	@Value("${bombardier.webSocketUrl:ws://localhost:8081/ws}")
	private String webSocketUrl;

	private URI uri;

	public WebSocketBasedBombEngine(BomberContextRegistry registry, WebSocketClient client) {
		super(registry);
		this.client = new BombardierWebSocketClient(client);
	}

	@Override
	protected void doEachExecute(BomberContext context, BombardierRequest request) {
		Date startTime = new Date();
		client.execute(uri, request, webSocketMessage -> {
			String message = webSocketMessage.getPayloadAsText();
			if (isMetric(message)) {
				this.fireMetric(context, getMetric(message));
			} else {
				try {
					this.fireEachExecute(context, new Result(startTime, new Date(), getResponse(message)));
				} catch (JsonProcessingException e) {
					log.error("failed to process websocket message", e);
				}
			}
		}).block(Duration.ofHours(1));
	}

	protected boolean isMetric(String message) {
		return message.length() < 20;
	}

	protected int getMetric(String message) {
		return Integer.parseInt(message);
	}

	protected BombardierResponse getResponse(String message) throws JsonProcessingException {
		return JsonUtils.fromJson(message, BombardierResponse.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.uri = new URI(webSocketUrl);
	}

	@Override
	public void destroy() throws Exception {
		super.destroy();
	}
}
