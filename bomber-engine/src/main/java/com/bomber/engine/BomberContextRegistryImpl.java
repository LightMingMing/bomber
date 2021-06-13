package com.bomber.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bomber.engine.model.BomberContext;

public class BomberContextRegistryImpl implements BomberContextRegistry {

	private final Map<Long, BomberContext> cache = new ConcurrentHashMap<>();

	@Override
	public BomberContext get(Long id) {
		return cache.get(id);
	}

	@Override
	public void register(BomberContext ctx) {
		cache.put(ctx.getId(), ctx);
	}

	@Override
	public void unregister(BomberContext ctx) {
		cache.remove(ctx.getId());
	}

}
