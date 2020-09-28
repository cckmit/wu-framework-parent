package com.wu.framework.easy.stereotype.upsert;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * description
 *
 * @author 吴佳伟
 * @date 2020/7/14 下午6:43
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@EasyTableFile(indexType = EasyTableFile.CustomTableFileIndexType.UNIQUE)
public @interface EasyUnique {

    @AliasFor(annotation = EasyTableFile.class)
    String value() default "";

    @AliasFor(annotation = EasyTableFile.class)
    String name() default "";

    @AliasFor(annotation = EasyTableFile.class,attribute = "type")
    String type() default "";

    @AliasFor(annotation = EasyTableFile.class,attribute = "comment")
    String comment() default "";
}
