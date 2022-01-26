package com.wu.framework.inner.layer.stereotype.analyze;

import com.wu.framework.inner.layer.CamelAndUnderLineConverter;
import com.wu.framework.inner.layer.stereotype.LayerClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;

/**
 * description layer 解析适配器
 *
 * @author Jia wei Wu
 * @date 2021/4/1 下午3:32
 */
public interface LayerClassAnalyzeAdapter extends LayerAnalyzeAdapter {


    /**
     * description 解析class
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/7 下午1:47
     */
    default LayerClass analyze(Class clazz) {
        LayerClass mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, LayerClass.class);
        if (ObjectUtils.isEmpty(mergedAnnotation)|| ObjectUtils.isEmpty(mergedAnnotation.name())) {
            mergedAnnotation = new LayerClass() {

                /**
                 * Returns the annotation type of this annotation.
                 *
                 * @return the annotation type of this annotation
                 */
                @Override
                public Class<? extends Annotation> annotationType() {
                    return LayerClass.class;
                }

                /**
                 * class name 例如:layer_class
                 *
                 * @return
                 */
                @Override
                public String name() {
                    return CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
                }
            };
        }
        return mergedAnnotation;
    }


}
