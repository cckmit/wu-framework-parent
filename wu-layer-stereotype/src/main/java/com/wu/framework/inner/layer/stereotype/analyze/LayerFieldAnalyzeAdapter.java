package com.wu.framework.inner.layer.stereotype.analyze;

import com.wu.framework.inner.layer.CamelAndUnderLineConverter;
import com.wu.framework.inner.layer.stereotype.Layer;
import com.wu.framework.inner.layer.stereotype.LayerDefault;
import com.wu.framework.inner.layer.stereotype.LayerField;
import lombok.val;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * description layer 解析适配器
 *
 * @author Jia wei Wu
 * @date 2021/4/1 下午3:32
 */
public interface LayerFieldAnalyzeAdapter extends LayerAnalyzeAdapter {


    default List<AnalyzeField>  analyze(Class clazz) {
        List<AnalyzeField> analyzeFieldList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            LayerField layerField = AnnotatedElementUtils.findMergedAnnotation(field, LayerField.class);
            if (ObjectUtils.isEmpty(layerField) || layerField.exist()) {
                AnalyzeField analyzeField = AnalyzeField.builder().fieldName(field.getName()).convertedFieldName(CamelAndUnderLineConverter.humpToLine2(field.getName())).build();
                if (!ObjectUtils.isEmpty(layerField)) {
                    analyzeField.setFieldIndexType(layerField.indexType());
                }
                analyzeFieldList.add(analyzeField);
            }
        }
        return analyzeFieldList;
    }

}
