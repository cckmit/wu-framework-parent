package com.wu.framework.inner.lazy.hbase.expland.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Hbase配置类
 *
 * @author sixmonth
 * @Date 2019年5月13日
 */
@Data
@ConfigurationProperties(prefix = HBaseConfigProperties.CONF_PREFIX)
public class HBaseConfigProperties {

    public static final String CONF_PREFIX = "spring.hbase";

    @Deprecated
    private Map<String, String> confMaps;

    /**
     * zookeeper1:2181,zookeeper2:2181
     */
    private String zookeeperQuorum;

    public Map<String, String> getConfMaps() {
        return confMaps;
    }

    public void setConfMaps(Map<String, String> confMaps) {
        this.confMaps = confMaps;
    }
}