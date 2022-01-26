package com.wu.framework.inner.layer.stereotype.proxy;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


/**
 * description  层代理
 *
 * @author Jia wei Wu
 * @date 2021/4/7 下午3:14
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Indexed
public @interface LayerProxy {


    /**
     * description 代理接口类
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/7 下午3:15
     */
    Class proxyInterface();
}
