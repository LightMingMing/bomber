package com.bomber.engine;


import com.bomber.engine.model.BomberContext;

public interface BomberContextRegistry {

	BomberContext get(String id);

	void register(BomberContext ctx);

	void unregister(BomberContext ctx);
}
