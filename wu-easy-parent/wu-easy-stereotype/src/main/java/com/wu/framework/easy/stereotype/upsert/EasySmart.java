package com.wu.framework.easy.stereotype.upsert;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * description EasySmart 灵性
 *
 * @author Jia wei Wu
 * @date 2020/12/14 下午12:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface EasySmart {

    /**
     * 表名
     *
     * @return
     */
    @AliasFor(attribute = "tableName")
    String value() default "";

    @AliasFor(attribute = "value")
    String tableName() default "";

    /**
     * 完善表
     *
     * @return
     */
    boolean perfectTable() default false;

    /**
     * 数据下钻
     * the field use Annotation with {@link com.wu.framework.easy.stereotype.upsert.SmartMark}
     */
    boolean dataDrillDown() default false;

    /**
     * 表注释
     *
     * @return String
     */
    String comment() default "";

    /**
     * kafka  schema 名称
     *
     * @return String
     */
    String kafkaSchemaName() default "";

    /**
     * kafka 主题 为空使用类名
     *
     * @return String
     */
    String kafkaTopicName() default "";

    /**
     * kafka code编码
     *
     * @return String
     */
    String kafkaCode() default "";

    /**
     * 数据库名 schema
     *
     * @return String
     */
    String schema() default "";

    /**
     * Elasticsearch 索引前缀
     *
     * @return String
     */
    String indexPrefix() default "";

    /**
     * Elasticsearch 索引时间格式
     *
     * @return String
     */
    String indexFormat() default "";

    /**
     * Elasticsearch 索引后缀
     */
    String indexSuffix() default "";

    /**
     * Elasticsearch 索引类型
     */
    String indexType() default "";

    /**
     * redis key
     */
    String redisKey() default "";

    /**
     * Hbase 行名称
     * 弃用原因 实体不绑定行
     *
     * @return
     */
    @Deprecated
    String hBaseRow() default "";

    /**
     * 列族
     *
     * @return
     */
    String columnFamily() default "";

    /**
     * 智能填充bean属性
     * 针对数据源 如mysql查询结果、http请求结果中包含的数据字段不再当前对象中
     */
    boolean smartFillField() default false;
}
