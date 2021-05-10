package com.bomber.function.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class CachedDataSourceManager implements DataSourceManager {

	public static final CachedDataSourceManager INSTANCE = new CachedDataSourceManager();

	private static final Logger logger = LoggerFactory.getLogger(CachedDataSourceManager.class);

	private static final int poolSize = Runtime.getRuntime().availableProcessors() * 2;

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(INSTANCE::destroy, "shuttingDownDataSource"));
	}

	private final Map<JdbcConfig, DataSource> cachedDataSources = new ConcurrentHashMap<>();

	private CachedDataSourceManager() {
	}

	// HikariPool-{database}@{host}
	// jdbc:***://localhost:3306/database?properties
	protected static String generatePoolName(String jdbcUrl) {
		int hostBeginIndex = jdbcUrl.indexOf("//") + 2;
		if (hostBeginIndex > 1 && hostBeginIndex < jdbcUrl.length()) {
			int hostEndIndex = jdbcUrl.indexOf('/', hostBeginIndex);
			if (hostEndIndex > hostBeginIndex) {
				String host = jdbcUrl.substring(hostBeginIndex, hostEndIndex);
				int databaseBeginIndex = hostEndIndex + 1;
				if (databaseBeginIndex < jdbcUrl.length()) {
					String database;
					int databaseEndIndex = jdbcUrl.indexOf('?', databaseBeginIndex);
					if (databaseEndIndex > databaseBeginIndex) {
						database = jdbcUrl.substring(databaseBeginIndex, databaseEndIndex);
					} else {
						database = jdbcUrl.substring(databaseBeginIndex);
					}
					return "HikariPool-" + database + "@" + host;
				}
			}
		}
		return null;
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

	public void destroy() {
		if (cachedDataSources.size() > 0) {
			logger.info("Shutting down cached data sources");
			cachedDataSources.forEach((config, dataSource) -> ((HikariDataSource) dataSource).close());
		}
	}
}
