package com.yesmywine.email;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.yesmywine.db.base.bean.DatabaseType;
import com.yesmywine.db.base.bean.DefaultData;
import com.yesmywine.util.basic.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by WANG, RUIQING on 11/30/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Configuration
@EnableCaching
@EnableJpaRepositories(basePackages = "com.*")
@ComponentScan(basePackages = {"com"})
@EnableRedisRepositories
@PropertySource("classpath:db.properties")
public class DBConfig {
    private String scanPackage = "com";

    private Logger logger = LoggerFactory.getLogger(DBConfig.class);

    private String dialect = Database.JDBC_DIALECT;
    private String url = Database.EMAIL_URL;
    private String userName = Database.JDBC_USERNAME;
    private String password = Database.JDBC_PASSWORD;
    private String driverClassName = Database.JDBC_DRIVER;
    private String datasourceInitialSize = Database.DATASOURCE_INITIALSIZE;
    private String datasourceMinIdle = Database.DATASOURCE_MINIDLE;
    private String datasourceMaxActive = Database.DATASOURCE_MAXACTIVE;
    private String datasourcemaxWait = Database.DATASOURCE_MACWAIT;
    private String datasourceTimeBetweenEvictionRunsMillis = Database.DATASOURCE_TIMEBETWEENEVICTIONRUNSMILLIS;
    private String datasourceMinEvictableIdleTimeMillis = Database.DATASOURCE_MINEVICTABLEIDLETIMEMILLIS;
    private String datasourceValidationQuery = Database.DATASOURCE_VALIDATIONQUERY;
    private String datasourceTestWhileIdle = Database.DATASOURCE_TESTWHILEIDLE;
    private String datasourceTestOnBorrow = Database.DATASOURCE_TESTONBORROW;
    private String datasourceTestOnReturn = Database.DATASOURCE_TESTONRETURN;
    private String datasourceFilters = Database.DATASOURCE_FILTERS;
    private String datasourceLogSlowSql = Database.DATASOURCE_LOGSHOWSQL;

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("loginUsername", "root");
        reg.addInitParameter("loginPassword", "1qaz2wsx");
        reg.addInitParameter("logSlowSql", datasourceLogSlowSql);
        return reg;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() {

        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(url);
        datasource.setUsername(userName);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(Integer.valueOf(datasourceInitialSize));
        datasource.setMinIdle(Integer.valueOf(datasourceMinIdle));
        datasource.setMaxActive(Integer.valueOf(datasourceMaxActive));
        datasource.setMaxWait(Integer.valueOf(datasourcemaxWait));
        datasource.setTimeBetweenEvictionRunsMillis(Long.valueOf(datasourceTimeBetweenEvictionRunsMillis));
        datasource.setMinEvictableIdleTimeMillis(Long.valueOf(datasourceMinEvictableIdleTimeMillis));
        datasource.setValidationQuery(datasourceValidationQuery);
        datasource.setTestWhileIdle("true".equals(datasourceTestWhileIdle)?true:false);
        datasource.setTestOnBorrow("true".equals(datasourceTestOnBorrow)?true:false);
        datasource.setTestOnReturn("true".equals(datasourceTestOnReturn)?true:false);
        try {
            datasource.setFilters(datasourceFilters);
        } catch (SQLException e) {
            logger.error("druid configuration initialization filter", e);
        }

        return datasource;
    }

    //@Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan(scanPackage);
        factoryBean.setDataSource(dataSource());
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(dialect);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        return factoryBean;
    }

    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setPackagesToScan(scanPackage);
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", dialect);
        hibernateProperties.put("hibernate.show_sql", true);
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", true);
        hibernateProperties.put("hibernate.format_sql", true);
//         hibernateProperties.put("hibernate.hbm2ddl.auto", "create");//自动建表
        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        return sessionFactoryBean;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory().getObject());
        tm.setDataSource(dataSource());
        return tm;
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName("88.88.88.211");
        connectionFactory.setPort(6379);
        connectionFactory.setPort(6379);
        return connectionFactory;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // Number of seconds before expiration. Defaults to unlimited (0)
//		cacheManager.setDefaultExpiration(33); //设置key-value超时时间
        return cacheManager;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory());
        return redisTemplate;
    }

//	@Bean
//	public StringRedisTemplate strRedisTemplate() {
//		StringRedisTemplate redisTemplate = new StringRedisTemplate();
//		redisTemplate.setConnectionFactory(connectionFactory());
//		return redisTemplate;
//	}

//	@Bean
//	public RedisConnectionFactory jedisConnectionFactory() {
////		RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration() .master("mymaster")
////				.sentinel("88.88.88.211", 6379) ;
//////				.sentinel("127.0.0.1", 26380);
////		return new JedisConnectionFactory(sentinelConfig);
//		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
//		connectionFactory.setHostName("88.88.88.211");
//		connectionFactory.setPort(6379);
//		return connectionFactory;
//	}

}
