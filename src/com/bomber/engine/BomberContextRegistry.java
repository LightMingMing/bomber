package com.bomber.engine;

public interface BomberContextRegistry {

	BomberContext get(String ctxId);

	void registerBomberContext(BomberContext ctx);

	void unregisterBomberContext(BomberContext ctx);
}
