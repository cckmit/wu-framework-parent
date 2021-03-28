package com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype;

import com.wu.framework.easy.stereotype.upsert.EasySmartField;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


@Deprecated
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@EasySmartField(indexType = EasySmartField.TableFileIndexType.AUTOMATIC, comment = "主键")
public @interface CustomId {
    @AliasFor(annotation = EasySmartField.class, attribute = "value")
    String value() default "";

    @AliasFor(annotation = EasySmartField.class, attribute = "name")
    String name() default "";


}
