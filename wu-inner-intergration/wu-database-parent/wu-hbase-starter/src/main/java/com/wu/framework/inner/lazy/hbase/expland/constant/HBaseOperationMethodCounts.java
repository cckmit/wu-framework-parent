package com.wu.framework.inner.lazy.hbase.expland.constant;

import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe : 常量 弃用使用策略方式调整为方法上直接找到数据
 * @date : 2021/3/29 7:34 下午
 */
@Deprecated
public class HBaseOperationMethodCounts {


    public static final String MISS = "miss";

    /**
     * {@link com.wu.framework.inner.lazy.hbase.expland.persistence.HBaseOperation#insert(Object)}
     */
    public static final String INSERT = "insert";// 数据插入

    /**
     * {@link com.wu.framework.inner.lazy.hbase.expland.persistence.HBaseOperation#insertList(List)}
     */
    public static final String INSERT_LIST="insertList";// 批量数据批量插入

    /**
     * {@link com.wu.framework.inner.lazy.hbase.expland.persistence.HBaseOperation#upsert(Object)}
     */
    public static final String UPSERT = "upsert"; // 数据更新
    /**
     * {@link com.wu.framework.inner.lazy.hbase.expland.persistence.HBaseOperation#upsertList(List)}
     */
    public static final String UPSERT_LIST = "upsertList"; //批量 数据更新

}
