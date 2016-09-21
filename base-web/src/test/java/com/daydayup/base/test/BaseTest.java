package com.daydayup.base.test;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(value = "classpath:/applicationContext.xml")
public abstract class BaseTest
		extends 
		org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests {

	@Override
	@Resource(name = "baseBaseDS")
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
}
