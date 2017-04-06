package com.softwhistle.util;

import javax.sql.DataSource;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcDataSourceFactory {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcDataSourceFactory.class);

	public static DataSource of(String datasourceName, Config config) {
		final BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(config.getString("url"));
		dataSource.setUsername(config.getString("username"));
		dataSource.setPassword(config.getString("password"));
		dataSource.setDriverClassName(config.getString("driverClassName"));
		LOG.info("Resolved data source: name={}; url={}", new Object[] {
			datasourceName, config.getString("url")
		});
		return dataSource;
	}
}