package com.wu.framework.inner.lazy.hbase.expland.persistence.proxy;

import com.wu.framework.inner.layer.stereotype.proxy.LayerProxy;
import com.wu.framework.inner.layer.stereotype.proxy.ProxyStrategicApproach;
import com.wu.framework.inner.lazy.hbase.expland.persistence.HBaseOperation;
import com.wu.framework.inner.lazy.hbase.expland.persistence.method.HBaseOperationMethodAdapter;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/3/27 10:57 下午
 */
@ConditionalOnBean(Connection.class)
@LayerProxy(proxyInterface = HBaseOperation.class)
public class HBaseOperationProxy implements InvocationHandler, InitializingBean {

    private final List<HBaseOperationMethodAdapter> hBaseOperationMethodList;
    private final Connection connection;

    private Map<String, HBaseOperationMethodAdapter> HBASE_OPERATION_METHOD_MAP;

    public HBaseOperationProxy(List<HBaseOperationMethodAdapter> hBaseOperationMethodList, Connection connection) {
        this.hBaseOperationMethodList = hBaseOperationMethodList;
        this.connection = connection;
    }


    // TODO 名称相同的方法不区分类型无法做到重载
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (HBASE_OPERATION_METHOD_MAP.containsKey(method.getName())) {

            return HBASE_OPERATION_METHOD_MAP.get(method.getName()).run(HBaseOperationMethodAdapter.HBaseExecuteParams.build().setConnection(connection).setObjects(args));
        } else {
            throw new RuntimeException(String.format("Can't find a way %s", method.getName()));
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        HBASE_OPERATION_METHOD_MAP = hBaseOperationMethodList.stream().collect(Collectors.toMap(hBaseOperationMethod -> {
            ProxyStrategicApproach mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(hBaseOperationMethod.getClass(), ProxyStrategicApproach.class);
            return mergedAnnotation.methodName();
        }, m -> m));

    }
}