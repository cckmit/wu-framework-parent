package com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype;

import com.wu.framework.inner.layer.data.DefaultIEnum;
import com.wu.framework.inner.layer.data.IEnum;
import com.wu.framework.inner.layer.stereotype.LayerField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * description 数据库字段注解  支持兼容 @JsonProperty
 *
 * @author Jia wei Wu
 * @date 2020/7/16 上午9:17
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@LayerField
public @interface LazyTableField {

    /**
     * 字段名
     */
    @AliasFor(attribute = "name")
    String value() default "";

    @AliasFor(attribute = "value")
    String name() default "";

    /**
     * 字段类型
     */
    // 数据库字段 当前支持 varchar number  int 默认是 varchar
    String type() default "";

    /**
     * 字段描述
     */
    String comment() default "";

    /**
     * 是否存在
     */
    boolean exist() default true;


    /**
     * 字段版本
     */
    int version() default 1;

    int scale() default 0;

    /**
     * 参数
     */
    String parameters() default "";

    /**
     * 可选的
     */
    boolean optional() default true;

    /**
     * 数据为空的时候使用字段默认值
     */
    String fieldDefaultValue() default "";

    /**
     * 转换指定类型的枚举 DefaultIEnum 不转换  转换失败默认是-1
     */
    Class<? extends IEnum> iEnum() default DefaultIEnum.class;

    /**
     * 数据格式
     */
    String dataType() default "";

    /**
     * 转换内容分隔符
     *
     * @return
     * @see IEnum#getConvertContentSeparator()
     */
    @Deprecated
    String[] convertContentSeparator() default {","};

    /**
     * 通过获取字典类型转换
     *
     * @return
     */
    String convert() default "";


    /**
     * 字段索引类型(数据库)
     */
    @AliasFor(attribute = "indexType",annotation =LayerField.class )
    LayerField.LayerFieldType indexType() default LayerField.LayerFieldType.FILE_TYPE;

    @Getter
    @AllArgsConstructor
    enum FileType {
        STRING(Collections.singletonList(String.class), " varchar(255) "),
        BYTE(Arrays.asList(Byte.class, byte.class), " varbinary(1024) "),
        INTEGER(Arrays.asList(Integer.class, int.class), " int(11) "),
        LONG(Arrays.asList(Long.class, long.class), " bigint "),
        LOCAL_DATE_TIME(Arrays.asList(LocalDateTime.class, Timestamp.class), " datetime "),
        LOCAL_DATE(Arrays.asList(LocalDate.class, Date.class), " date "),
        DOUBLE(Arrays.asList(Double.class, double.class), " double "),
        FLOAT(Arrays.asList(Float.class, float.class), " float "),
        BINARY(Arrays.asList(File.class, InputStream.class), " Blob ");
        private static Map<Class, String> TYPE_MAP = new HashMap<>();

        static {
            for (FileType fileType : values()) {
                Map<Class, String> classStringMap =
                        fileType.clazz.stream().collect(Collectors.toMap(Function.identity(), c -> fileType.type));
                TYPE_MAP.putAll(classStringMap);
            }
        }

        private List<Class> clazz;
        private String type;

        public static String getTypeByClass(Class clazz) {
            for (FileType fileType : values()) {
                if (fileType.clazz.contains(clazz)) {
                    return fileType.type;
                }
            }
            return STRING.type;
        }
    }


    @AllArgsConstructor
    @Getter
    enum JavaSchemaDataType {
        STRING(String.class, "string"),
        INT(Integer.class, "int32"),
        LONG(Long.class, "int64"),
        FLOAT(Float.class, "float32"),
        DOUBLE(Double.class, "double"),
        BOOLEAN(Boolean.class, "boolean"),
        LOCAL_DATETIME(LocalDateTime.class, "boolean"),
        LOCAL_DATE(LocalDate.class, "LocalDate"),
        DATE(Date.class, "Date"),
        TIMESTAMP(Timestamp.class, "Timestamp"),
        TIME(Time.class, "time");

        private static final Map<Class, String> JAVA_SCHEMA_TYPE =
                Arrays.stream(values()).
                        collect(Collectors.toMap(JavaSchemaDataType::getClazz, JavaSchemaDataType::getAlias));
        private Class clazz;
        private String alias;

        public static String getAlias(Class clazz) {
            return JAVA_SCHEMA_TYPE.getOrDefault(clazz, STRING.alias);
        }

        public static String getESAlias(Class clazz) {
            for (JavaSchemaDataType value : values()) {
                if (value.getClazz() == clazz) {
                    return value.getAlias();
                }
            }
            return STRING.getAlias();
        }
    }

}
