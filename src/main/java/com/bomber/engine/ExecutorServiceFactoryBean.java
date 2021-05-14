package com.bomber.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ironrhino.core.util.NameableThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("bombingExecutor")
public class ExecutorServiceFactoryBean implements FactoryBean<ExecutorService>, InitializingBean, DisposableBean {

	private ExecutorService executorService;

	@Value("${bombingExecutor.keepAliveTime:60}")
	private long keepAliveTime = 60;

	@Value("${bombingExecutor.queueCapacity:10000}")
	private int queueCapacity = 10000;

	@Override
	public void afterPropertiesSet() {
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, keepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(queueCapacity), new NameableThreadFactory("bombingExecutor"),
				(runnable, executor) -> {
					log.error("{} is rejected: {}", runnable, executor);
				});
		tpe.allowCoreThreadTimeOut(true);
		executorService = tpe;
	}

	@Override
	public void destroy() throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
	}

	@Override
	public ExecutorService getObject() {
		return executorService;
	}

	@Override
	public Class<? extends ExecutorService> getObjectType() {
		return ExecutorService.class;
	}
}
