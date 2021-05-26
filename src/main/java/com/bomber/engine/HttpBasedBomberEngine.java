package com.bomber.engine;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.bomber.engine.model.BomberContext;
import com.bomber.engine.model.Result;
import com.bomber.engine.monitor.TestingListener;
import com.bomber.engine.rpc.BombardierRequest;
import com.bomber.engine.rpc.BombardierResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpBasedBomberEngine extends SingleThreadBomberEngine {

	private final BombardierService bombardierService;

	public HttpBasedBomberEngine(BomberContextRegistry registry,
								 BombardierService bombardierService,
								 TestingListener... listeners) {
		super(registry, listeners);
		this.bombardierService = bombardierService;
	}

	@Override
	protected void doEachExecute(BomberContext ctx, BombardierRequest request) {
		Date startTime = new Date();
		BombardierResponse response = bombardierService.execute(request);
		this.fireEachExecute(ctx, new Result(startTime, new Date(), response));
	}

}
