package com.wu.framework.inner.lazy.hbase.expland.persistence.stereotype;

import com.wu.framework.inner.layer.stereotype.LayerField;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@LayerField(indexType = LayerField.LayerFieldType.FILE_TYPE)
public @interface HBaseTableUnique {


}
