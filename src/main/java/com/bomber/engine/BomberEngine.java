package com.bomber.engine;

public interface BomberEngine {

	void execute(BomberContext ctx);

	void continueExecute(String ctxId);

	void pauseExecute(String ctxId);

}
