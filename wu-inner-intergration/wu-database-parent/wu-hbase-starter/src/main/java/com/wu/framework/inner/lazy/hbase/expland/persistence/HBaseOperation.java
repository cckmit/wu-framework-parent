package com.wu.framework.inner.lazy.hbase.expland.persistence;


import com.wu.framework.inner.layer.stereotype.proxy.ProxyStrategicApproach;
import com.wu.framework.inner.lazy.hbase.expland.constant.HBaseOperationMethodCounts;
import com.wu.framework.inner.lazy.hbase.expland.persistence.method.HBaseOperationInsertListMethodAdapter;
import com.wu.framework.inner.lazy.hbase.expland.persistence.method.HBaseOperationUpsertListMethodAdapter;

import java.util.List;

/**
 * description HBaseOperation
 * HBase 操作使用
 *
 * @author Jia wei Wu
 * @date 2021/3/26 下午5:10
 */
public interface HBaseOperation {


    /**
     * @param <T>
     * @return t
     * @describe 新增数据
     * @author Jia wei Wu
     * @date 2021/3/27 9:21 下午
     **/
    @ProxyStrategicApproach(proxyClass = HBaseOperationInsertListMethodAdapter.class)
    <T> T insert(T t);

    /**
     * description  插入数据集合
     *
     * @param t
     * @param <T>
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/8 上午9:37
     */
    @ProxyStrategicApproach(proxyClass = HBaseOperationInsertListMethodAdapter.class)
    <T> T insertList(List<T> t);


    /**
     * @param <T>
     * @return t
     * @describe 更新或者插入数据
     * @author Jia wei Wu
     * @date 2021/3/27 9:21 下午
     **/
    @ProxyStrategicApproach(proxyClass = HBaseOperationUpsertListMethodAdapter.class)
    <T> T upsert(T t);

    /**
     * description 批量更新或者插入数据
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/9 下午1:05
     */
    @ProxyStrategicApproach(proxyClass = HBaseOperationUpsertListMethodAdapter.class)
    <T> T upsertList(List<T> t);


}
