package com.wu.framework.inner.lazy.hbase.expland.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/3/29 10:11 下午
 */
@Slf4j
@ConditionalOnProperty(prefix = HBaseConfigProperties.CONF_PREFIX, value = "zookeeper-quorum")
public class HBaseOperationConfig {

    private static ExecutorService pool = Executors.newScheduledThreadPool(20);    //设置hbase连接池
    private final HBaseConfigProperties hBaseConfigProperties;

    public HBaseOperationConfig(HBaseConfigProperties hBaseConfigProperties) {
        this.hBaseConfigProperties = hBaseConfigProperties;
    }


    @Bean
    public Connection hBaseClientConnection() throws IOException {
        Configuration conf = HBaseConfiguration.create();

        Map<String, String> confMap = new HashMap<>();
        if(ObjectUtils.isEmpty(hBaseConfigProperties.getZookeeperQuorum())){
            throw new IllegalArgumentException("could not found zookeeper address of empty");
        }
        // 配置zookeeper 集群地址
        confMap.put("hbase.zookeeper.quorum",hBaseConfigProperties.getZookeeperQuorum());
        for (Map.Entry<String, String> confEntry : confMap.entrySet()) {
            conf.set(confEntry.getKey(), confEntry.getValue());
        }
        log.info("init hbaseClientConnection success");
        return ConnectionFactory.createConnection(conf, pool);
    }

    @Bean
    public Admin hBaseClientAdmin(Connection connection) throws IOException {
        return connection.getAdmin();
    }
}
