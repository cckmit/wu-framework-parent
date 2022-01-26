package com.wu.framework.inner.lazy.hbase.expland.analyze;

import com.wu.framework.inner.layer.CamelAndUnderLineConverter;
import com.wu.framework.inner.layer.stereotype.LayerClass;
import com.wu.framework.inner.layer.stereotype.LayerField;
import com.wu.framework.inner.layer.stereotype.analyze.AnalyzeField;
import com.wu.framework.inner.layer.stereotype.analyze.LayerClassAnalyzeAdapter;
import com.wu.framework.inner.lazy.hbase.expland.persistence.stereotype.HBaseTable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/4/8 7:07 下午
 */
public class HBaseLayerAnalyzeAdapter<P> implements LayerClassAnalyzeAdapter{



    /**
     * description 分析字段
     * @param
     * @return
     * @exception/throws
     * @author 吴佳伟
     * @date 2021/4/25 下午1:39
     */
    public List<AnalyzeField> analyzeField(Class clazz) {
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


    /**
     * description 分析class
     *
     * @param
     * @return
     * @exception/throws
     * @author 吴佳伟
     * @date 2021/4/25 下午1:39
     */
    public HBaseTable analyzeClass(Class clazz) {
        HBaseTable mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, HBaseTable.class);
        LayerClass layerClass = analyze(clazz);
        return new HBaseTable() {

            /**
             * Returns the annotation type of this annotation.
             *
             * @return the annotation type of this annotation
             */
            @Override
            public Class<? extends Annotation> annotationType() {
                return HBaseTable.class;
            }

            @Override
            public String namespace() {
                return null != mergedAnnotation  ? mergedAnnotation.namespace() :"default";
            }

            @Override
            public String tableName() {
                return layerClass.name();
            }

            @Override
            public String columnFamily() {
                return null != mergedAnnotation & !ObjectUtils.isEmpty(mergedAnnotation.columnFamily()) ? mergedAnnotation.columnFamily() : CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName().replaceAll("\\.", "_"));
            }

            @Override
            public boolean perfectTable() {
                return mergedAnnotation.perfectTable();
            }
        };

    }
}
