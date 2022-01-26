package com.wu.framework.inner.lazy.database.expand.database.persistence.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * description 懒人数据源配置 (可选)
 * 默认使用 MysqlDataSource
 *
 * @author 吴佳伟
 * @date 2021/4/23 下午1:39
 */
public class LazyDataSourceConfig {



}
