package com.bomber.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class CachedDataSourceManager implements DataSourceManager, DisposableBean {

	private static final int poolSize = Runtime.getRuntime().availableProcessors() * 2;

	private final Map<JdbcConfig, DataSource> cachedDataSources = new ConcurrentHashMap<>();

	private DataSource createHikariDataSource(JdbcConfig config) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(config.getUrl());
		hikariConfig.setUsername(config.getUser());
		hikariConfig.setPassword(config.getPassword());
		// Fixed size
		hikariConfig.setMinimumIdle(poolSize);
		hikariConfig.setMaximumPoolSize(poolSize);
		hikariConfig.setConnectionTimeout(10000);
		hikariConfig.setMaxLifetime(3600000); // 1 hour
		return new HikariDataSource(hikariConfig);
	}

	@Override
	public DataSource getDataSource(String url, String user, String password) {
		return cachedDataSources.computeIfAbsent(new JdbcConfig(url, user, password), this::createHikariDataSource);
	}

	@Override
	public void destroy() {
		cachedDataSources.forEach((config, dataSource) -> ((HikariDataSource) dataSource).close());
	}
}
