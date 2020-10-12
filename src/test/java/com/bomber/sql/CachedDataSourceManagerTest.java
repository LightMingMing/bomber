package com.bomber.sql;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CachedDataSourceManager.class)
public class CachedDataSourceManagerTest {

	@Autowired
	private DataSourceManager dataSourceManager;

	@Test
	public void testCache() {
		DataSource d1 = dataSourceManager.getDataSource("jdbc:h2:mem:test;", "", "");
		DataSource d2 = dataSourceManager.getDataSource("jdbc:h2:mem:test;", "", "");
		assertThat(d1).isEqualTo(d2);

		DataSource d3 = dataSourceManager.getDataSource("jdbc:h2:mem:test2;", "", "");
		assertThat(d1).isNotEqualTo(d3);
	}

}