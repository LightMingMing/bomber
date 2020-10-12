package com.bomber.sql;

import javax.sql.DataSource;

public interface DataSourceManager {

	DataSource getDataSource(String url, String user, String password);

}
