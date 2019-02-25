package com.yesmywine.util.basic;

import com.yesmywine.util.properties.PropertiesUtil;

/**
 * Created by by on 2017/9/7.
 */
public class Database {
    public static final String GOODS_URL = PropertiesUtil.getString("goods.jdbc.url", "db.properties");
    
    public static final String DIC_URL = PropertiesUtil.getString("dic.jdbc.url", "db.properties");
    
    public static final String EMAIL_URL = PropertiesUtil.getString("email.jdbc.url", "db.properties");

    public static final String SMS_URL = PropertiesUtil.getString("sms.jdbc.url", "db.properties");

    public static final String INVENTORY_URL = PropertiesUtil.getString("inventory.jdbc.url", "db.properties");

    public static final String LOGISTICS_URL = PropertiesUtil.getString("logistics.jdbc.url", "db.properties");

    public static final String USER_URL = PropertiesUtil.getString("user.jdbc.url", "db.properties");

    public static final String JDBC_DIALECT = PropertiesUtil.getString("jdbc.dialect", "db.properties");

    public static final String JDBC_USERNAME = PropertiesUtil.getString("jdbc.userName", "db.properties");

    public static final String JDBC_PASSWORD = PropertiesUtil.getString("jdbc.password", "db.properties");

    public static final String JDBC_DRIVER= PropertiesUtil.getString("jdbc.driver", "db.properties");

    public static final String DATASOURCE_INITIALSIZE= PropertiesUtil.getString("spring.datasource.initialSize", "db.properties");

    public static final String DATASOURCE_MINIDLE= PropertiesUtil.getString("spring.datasource.minIdle", "db.properties");

    public static final String DATASOURCE_MAXACTIVE= PropertiesUtil.getString("spring.datasource.maxActive", "db.properties");

    public static final String DATASOURCE_MACWAIT= PropertiesUtil.getString("spring.datasource.maxWait", "db.properties");

    public static final String DATASOURCE_TIMEBETWEENEVICTIONRUNSMILLIS= PropertiesUtil.getString("spring.datasource.timeBetweenEvictionRunsMillis", "db.properties");

    public static final String DATASOURCE_MINEVICTABLEIDLETIMEMILLIS= PropertiesUtil.getString("spring.datasource.minEvictableIdleTimeMillis", "db.properties");

    public static final String DATASOURCE_VALIDATIONQUERY= PropertiesUtil.getString("spring.datasource.validationQuery", "db.properties");

    public static final String DATASOURCE_TESTWHILEIDLE= PropertiesUtil.getString("spring.datasource.testWhileIdle", "db.properties");

    public static final String DATASOURCE_TESTONBORROW= PropertiesUtil.getString("spring.datasource.testOnBorrow", "db.properties");

    public static final String DATASOURCE_TESTONRETURN= PropertiesUtil.getString("spring.datasource.testOnReturn", "db.properties");

    public static final String DATASOURCE_FILTERS= PropertiesUtil.getString("spring.datasource.filters", "db.properties");

    public static final String DATASOURCE_LOGSHOWSQL= PropertiesUtil.getString("spring.datasource.logSlowSql", "db.properties");

}
