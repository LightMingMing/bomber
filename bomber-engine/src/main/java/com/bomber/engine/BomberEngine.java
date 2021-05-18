package com.bomber.engine;

import java.util.concurrent.Future;

import com.bomber.engine.model.BomberRequest;

/**
 * Bomber 执行引擎
 *
 * @author MingMing Zhao
 */
public interface BomberEngine {

	Future<?> execute(BomberRequest request);

	void pause(String id);

}
