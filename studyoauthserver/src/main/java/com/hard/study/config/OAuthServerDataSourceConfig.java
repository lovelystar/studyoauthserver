package com.hard.study.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class OAuthServerDataSourceConfig {
	
	@Autowired
	private Environment env;
	
	@Bean
	public DataSource dataSource() {
		
		String driverClass = env.getProperty("spring.datasource.driver-class-name");
		String jdbcUrl = env.getProperty("spring.datasource.url");
		String username = env.getProperty("spring.datasource.username");
		String password = env.getProperty("spring.datasource.password");
		
		DataSource dataSource = new DataSource();
		
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		
		// connection pool을 끊기지 않고 유지하기 위해 세팅
		// MySQL에선 필수적
		dataSource.setValidationInterval(30000);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestWhileIdle(true);
		dataSource.setInitialSize(10);
		dataSource.setMinIdle(10);
		
		return dataSource;
		
	}
	
    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        
    	return new DataSourceTransactionManager(dataSource());
        
    }
    
	@Bean
	public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {		
		
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource);
		sqlSessionFactory.setMapperLocations(applicationContext.getResources("classpath:mybatis/mapper/*.xml"));
		sqlSessionFactory.setTypeAliasesPackage("com.hard.study");
		
		return sqlSessionFactory;
		
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		
		return new SqlSessionTemplate(sqlSessionFactory);
		
	}
	
}
