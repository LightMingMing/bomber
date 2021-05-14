package com.bomber.engine;

import com.bomber.engine.model.BomberContext;

public interface BomberEngine {

	void execute(BomberContext ctx);

	void pauseExecute(String id);

}
