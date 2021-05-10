package com.bomber.function.sql;

import javax.sql.DataSource;

/**
 * 数据源管理
 *
 * @author MingMing Zhao
 */
public interface DataSourceManager {

	DataSource getDataSource(String url, String user, String password);

}
