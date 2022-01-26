package com.wu.framework.inner.layer.stereotype;


import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * description 层类注解
 *
 * @param
 * @author Jia wei Wu
 * @return
 * @exception/throws
 * @date 2021/4/1 下午2:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface LayerClass {

    /**
     * class name 例如:layer_class
     * @return
     */
    String name() default "";
}
