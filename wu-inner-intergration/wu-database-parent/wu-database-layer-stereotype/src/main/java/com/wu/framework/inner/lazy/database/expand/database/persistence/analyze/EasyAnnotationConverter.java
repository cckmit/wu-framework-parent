package com.wu.framework.inner.lazy.database.expand.database.persistence.analyze;



import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTable;
import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTableField;
import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTableFieldUnique;
import com.wu.framework.inner.layer.data.DefaultIEnum;
import com.wu.framework.inner.layer.data.IEnum;
import com.wu.framework.inner.layer.CamelAndUnderLineConverter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description 解析 LazyTable 中的参数
 *
 * @author Jia wei Wu
 * @date 2020/7/16 下午2:12
 */
public class EasyAnnotationConverter {

    /**
     * 默认
     */
    private final static String DEFAULT = "DEFAULT";

//    private final static Logger log = LoggerFactory.getLogger(EasyAnnotationConverter.class);

    /**
     * 获取  LazyTable 上的 kafkaTopicName
     *
     * @param clazz
     * @return
     */
    public static String getCustomTableValue(Class clazz) {
        LazyTable lazyTable = AnnotationUtils.getAnnotation(clazz, LazyTable.class);
        if (null != lazyTable && !ObjectUtils.isEmpty(lazyTable.tableName())) {
            return lazyTable.tableName();
        }
        return CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
    }





    /**
     * 获取 LazyTableFieldUnique 中的value
     *
     * @param clazz
     * @return
     */
    public static List<String> getCustomUniquePK(Class clazz) {
        List<String> uniqueList = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            LazyTableFieldUnique lazyTableFieldUnique = AnnotationUtils.getAnnotation(declaredField, LazyTableFieldUnique.class);
            if (null != lazyTableFieldUnique) {
                String pk = declaredField.getName();
                if (!ObjectUtils.isEmpty(lazyTableFieldUnique.value())) {
                    pk = lazyTableFieldUnique.value();
                }
                uniqueList.add(pk);
            }
        }
        return uniqueList;
    }

    /**
     * description 注解字典转换
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/8/4 下午4:39
     */
    public static Object annotationConvertConversion(Field field, Object fieldVal, Map<String, Map<String, String>> iEnumList) {
        LazyTableField lazyTableField = AnnotatedElementUtils.getMergedAnnotation(field, LazyTableField.class);
        // 确认枚举转换
        if (null != lazyTableField && !DefaultIEnum.class.equals(lazyTableField.iEnum()) && lazyTableField.iEnum().isEnum()) {
            Class<? extends IEnum> e = lazyTableField.iEnum();
            if (!ObjectUtils.isEmpty(fieldVal)) {
                if (fieldVal instanceof String) {
                    fieldVal = ((String) fieldVal).trim();
                }
                // 字典转换中获取需要分割的字符

                String[] delimiters = e.getEnumConstants()[0].getConvertContentSeparator();
//                      lazyTableField.convertContentSeparator();
                // 分割后的内容
                List<String> splitContent = new ArrayList<>();
                if (!ObjectUtils.isEmpty(delimiters)) {
                    splitContent = Arrays.asList(fieldVal.toString().split(String.join("|", delimiters)));
                } else {
                    splitContent = (List<String>) fieldVal;
                }
                // 转换后的字典
                List<String> res = new ArrayList<>();
                // 字典api转换
                if (!ObjectUtils.isEmpty(lazyTableField.convert())) {
                    Map<String, String> ConvertMap = iEnumList.get(lazyTableField.convert());
                    if (!ObjectUtils.isEmpty(ConvertMap)) {
                        for (String val : splitContent) {
                            if (ConvertMap.containsKey(val)) {
                                res.add(ConvertMap.get(val));
                                continue;
                            }
                        }
                    }
                }
                // 枚举转换
                Map<String, String> fieldMap = iEnumList.get(field.getName());
                loop:
                for (String val : splitContent) {
                    if (fieldMap.containsKey(val)) {
                        res.add(fieldMap.get(val));
                        continue loop;
                    }
//                    log.warn(" fail to find the val of:【{}】 in Enums:{} ", val, e);
                }
                if (ObjectUtils.isEmpty(res)) {
                    return e.getEnumConstants()[0].getDefaultCode();
                }
                return String.join(",", res);
            } else {
                return e.getEnumConstants()[0].getDefaultCode();
            }
        }
        // 不转换直接跳出
        return fieldVal;
    }

    /**
     * 收集类字典
     *
     * @param clazz
     * @return
     */
    public static Map<String, Map<String, String>> collectionConvert(Class clazz) {
        Map<String, Map<String, String>> enumMap = new HashMap<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            LazyTableField lazyTableField = AnnotatedElementUtils.getMergedAnnotation(declaredField, LazyTableField.class);
            if (null != lazyTableField && !DefaultIEnum.class.equals(lazyTableField.iEnum()) && lazyTableField.iEnum().isEnum()) {
                Class<? extends IEnum> e = lazyTableField.iEnum();
                Map<String, String> fieldMap = Arrays.stream(e.getEnumConstants()).collect(Collectors.toMap(IEnum::getItem, IEnum::getCode));
                enumMap.put(declaredField.getName(), fieldMap);
            }
        }
        return enumMap;
    }

    /**
     * 获取表名称
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class clazz) {
        LazyTable lazyTable = AnnotationUtils.getAnnotation(clazz, LazyTable.class);
        if (null != lazyTable && !ObjectUtils.isEmpty(lazyTable.tableName())) {
            return lazyTable.tableName();
        }
        return CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
    }

    /**
     * 获取 LazyTable comment
     *
     * @param clazz
     * @return
     */
    public static String getComment(Class clazz) {
        LazyTable lazyTable = AnnotationUtils.getAnnotation(clazz, LazyTable.class);
        if (null == lazyTable) {
            return "";
        }
        return lazyTable.comment();
    }


}
