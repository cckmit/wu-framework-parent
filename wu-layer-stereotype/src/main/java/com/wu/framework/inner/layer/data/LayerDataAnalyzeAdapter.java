package com.wu.framework.inner.layer.data;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface LayerDataAnalyzeAdapter {

    /**
     * @param extractList 中间存储数据对象 可以为空
     * @param objects     需要提取的对象
     * @return
     * @describe 复杂数据提取
     * @author Jia wei Wu
     * @date 2021/4/11 11:40 上午
     **/
    default List<List> extractData(List<List> extractList, Object... objects) {
        if (extractList == null) extractList = new ArrayList<List>();
        for (Object object : objects) {
            Class<?> aClass = object.getClass();
            // 类上没有注解 不下钻
            final LayerData lazyTable = AnnotatedElementUtils.findMergedAnnotation(aClass, LayerData.class);
            if (null == lazyTable || !lazyTable.dataDrillDown()) {
                extractList.add(Collections.singletonList(object));
                continue;
            }
            for (Field field : aClass.getDeclaredFields()) {
                field.setAccessible(true);
                SmartMark smartMarkField = AnnotationUtils.getAnnotation(field, SmartMark.class);
                if (null == smartMarkField) continue;
                Class<?> fieldType = field.getType();
                Object o = null;
                try {
                    o = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (null == o) continue;
                //
                if (Iterable.class.isAssignableFrom(fieldType)) {
                    extractList.add((List) o);
                    continue;
                }
                extractData(extractList, o);
            }
        }
        return extractList;
    }


    /**
     * @return
     * @describe 将List分割成多个List
     * @params
     * @author Jia wei Wu
     * @date 2020/12/6 5:34 下午
     **/
    default <T> List<List<T>> splitList(List<T> source, int groupSize) {
        int length = source.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(source.subList(fromIndex, toIndex));
        }
        return newList;
    }
}
