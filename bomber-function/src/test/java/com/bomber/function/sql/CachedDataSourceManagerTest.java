package com.bomber.function.sql;

import static com.bomber.function.sql.CachedDataSourceManager.generatePoolName;
import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

public class CachedDataSourceManagerTest {

	@Test
	public void testCache() {
		DataSourceManager dataSourceManager = CachedDataSourceManager.INSTANCE;
		DataSource d1 = dataSourceManager.getDataSource("jdbc:h2:mem:test;", "", "");
		DataSource d2 = dataSourceManager.getDataSource("jdbc:h2:mem:test;", "", "");
		assertThat(d1).isEqualTo(d2);

		DataSource d3 = dataSourceManager.getDataSource("jdbc:h2:mem:test2;", "", "");
		assertThat(d1).isNotEqualTo(d3);
	}

	@Test
	public void testGeneratePoolName() {
		assertThat(generatePoolName("jdbc:mysql://localhost/bomber")).endsWith("bomber@localhost");
		assertThat(generatePoolName("jdbc:mysql://localhost/bomber?createDatabaseIfNotExist=true")).endsWith("bomber@localhost");
		assertThat(generatePoolName("jdbc:mysql://localhost:3306/bomber")).endsWith("bomber@localhost:3306");
		assertThat(generatePoolName("jdbc:mysql://localhost:3306/bomber?createDatabaseIfNotExist=true")).endsWith("bomber@localhost:3306");
	}

	@Test
	public void testUnknownPoolName() {
		assertThat(generatePoolName("jdbc:mysql")).isNull();
		assertThat(generatePoolName("jdbc:mysql://localhost")).isNull();
		assertThat(generatePoolName("jdbc:mysql://localhost/")).isNull();
	}

}