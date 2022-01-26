package com.wu.framework.inner.layer.stereotype.proxy;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * description  层代理配置  //TODO
 *
 * @author Jia wei Wu
 * @date 2021/4/7 下午3:14
 */
public class LayerProxyConfig implements InitializingBean {


    private final List<InvocationHandler> invocationHandlerList;
    private final ConfigurableBeanFactory configurableBeanFactory;

    public LayerProxyConfig(List<InvocationHandler> invocationHandlerList, ConfigurableBeanFactory configurableBeanFactory) {
        this.invocationHandlerList = invocationHandlerList;
        this.configurableBeanFactory = configurableBeanFactory;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // 没有aop  需要通过 BeanFactoryPostProcessor 的实现类注册才可(无法注入bean)
        for (InvocationHandler invocationHandler : invocationHandlerList) {
            LayerProxy layerProxy = AnnotatedElementUtils.findMergedAnnotation(invocationHandler.getClass(), LayerProxy.class);
            if (null != layerProxy) {
                configurableBeanFactory.registerSingleton(layerProxy.proxyInterface().getSimpleName(),
                        Proxy.newProxyInstance(layerProxy.proxyInterface().getClassLoader(), new Class[]{layerProxy.proxyInterface()}, invocationHandler));
            }
        }
    }
}
