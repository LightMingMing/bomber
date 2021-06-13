package com.bomber.engine;


import com.bomber.engine.model.BomberContext;

public interface BomberContextRegistry {

	BomberContext get(Long id);

	void register(BomberContext ctx);

	void unregister(BomberContext ctx);
}
