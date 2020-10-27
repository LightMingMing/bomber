package com.bomber.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class CachedDataSourceManager implements DataSourceManager, DisposableBean {

	private static final int poolSize = Runtime.getRuntime().availableProcessors() * 2;

	private final Map<JdbcConfig, DataSource> cachedDataSources = new ConcurrentHashMap<>();

	// HikariPool-{database}@{host}
	protected static String generatePoolName(String jdbcUrl) {
		// Avoid UnsupportedConnectionStringException: Connector/J cannot handle a
		// connection string 'jdbc:h2:mem:test;'
		if (jdbcUrl.startsWith("jdbc:h2")) {
			return null;
		}
		HostInfo hostInfo = ConnectionUrl.getConnectionUrlInstance(jdbcUrl, null).getMainHost();
		return hostInfo == null ? null : "HikariPool-" + hostInfo.getDatabase() + "@" + hostInfo.getHost();
	}

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
		hikariConfig.setPoolName(generatePoolName(config.getUrl()));
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
