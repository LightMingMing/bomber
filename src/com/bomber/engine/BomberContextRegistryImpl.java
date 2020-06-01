package com.bomber.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class BomberContextRegistryImpl implements BomberContextRegistry {

	private final Map<String, BomberContext> bomberContextRegistry = new ConcurrentHashMap<>();

	public BomberContext get(String ctxId) {
		return bomberContextRegistry.get(ctxId);
	}

	public void registerBomberContext(BomberContext ctx) {
		bomberContextRegistry.put(ctx.getId(), ctx);
	}

	public void unregisterBomberContext(BomberContext ctx) {
		bomberContextRegistry.remove(ctx.getId());
	}

}
