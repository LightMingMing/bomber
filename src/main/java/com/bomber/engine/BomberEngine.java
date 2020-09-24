package com.bomber.engine;

public interface BomberEngine {

	void execute(BomberContext ctx);

	void pauseExecute(String id);

}
