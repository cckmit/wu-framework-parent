package com.wu.framework.inner.lazy.database.expand.database.persistence.config;


import com.mysql.cj.jdbc.MysqlDataSource;
import com.wu.framework.inner.lazy.database.expand.database.persistence.LazyOperation;
import com.wu.framework.inner.lazy.database.expand.database.persistence.proxy.LazyOperationProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;


public class LazyOperationProxyBeanConfig {


    /**
     * description 添加懒人数据源配置
     *
     * @param
     * @param dataSourceProperties
     * @return
     * @exception/throws
     * @author 吴佳伟
     * @date 2021/4/23 下午1:54
     */
//    @ConditionalOnBean(DataSourceProperties.class)
    @Bean(name = "lazyDataSource")
    public DataSource lazyDataSource(DataSourceProperties dataSourceProperties) {
        MysqlDataSource build = DataSourceBuilder.create().type(MysqlDataSource.class).build();
        build.setUrl(dataSourceProperties.getUrl());
        build.setUser(dataSourceProperties.getUsername());
        build.setPassword(dataSourceProperties.getPassword());
        return build;
    }

    /**
     * @param
     * @return
     * @describe 延迟操作代理
     * @author Jia wei Wu
     * @date 2021/4/17 7:34 下午
     **/
    @ConditionalOnBean(value = DataSource.class)
    @Bean
    public LazyOperation lazyOperation(LazyOperationProxy lazyOperationProxy) {
        return (LazyOperation) Proxy.newProxyInstance(LazyOperation.class.getClassLoader(), new Class[]{LazyOperation.class}, lazyOperationProxy);
    }


//    /**
//    * @describe 完美的惰性操作代理
//    * @param
//    * @return
//    * @author Jia wei Wu
//    * @date 2021/4/17 7:34 下午
//    **/
//    @Bean
//    public PerfectLazyOperation perfectLazyOperationProxy(PerfectLazyOperationProxy perfectLazyOperationProxy) {
//        return (PerfectLazyOperation) Proxy.newProxyInstance(PerfectLazyOperation.class.getClassLoader(), new Class[]{PerfectLazyOperation.class}, perfectLazyOperationProxy);
//    }


}

