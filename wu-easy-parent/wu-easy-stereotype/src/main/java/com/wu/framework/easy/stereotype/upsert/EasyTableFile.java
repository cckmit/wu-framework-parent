package com.wu.framework.easy.stereotype.upsert;


import com.wu.framework.easy.stereotype.upsert.ienum.DefaultIEnum;
import com.wu.framework.easy.stereotype.upsert.ienum.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * description 数据库字段注解  支持兼容 @JsonProperty
 *
 * @author 吴佳伟
 * @date 2020/7/16 上午9:17
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface EasyTableFile {

    @AliasFor(attribute = "name")
    String value() default "";

    @AliasFor(attribute = "value")
    String name() default "";

    // 数据库字段 当前支持 varchar number  int 默认是 varchar
    String type() default " varchar (20)";

    String comment() default "";

    boolean exist() default true;


    int version() default 1;

    int scale() default 0;

    String parameters() default "";

    boolean optional() default true;

    /**
     * 数据为空的时候使用字段默认值
     */
    String fieldDefaultValue() default "";

    /**
     * 转换指定类型的枚举 DefaultIEnum 不转换  转换失败默认是-1
     *
     * @return
     */
    Class<? extends IEnum> iEnum() default DefaultIEnum.class;

    /**
     * 数据格式
     *
     * @return
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
    String dictionary() default "";


    CustomTableFileIndexType indexType() default CustomTableFileIndexType.FILE_TYPE;

    enum CustomTableFileIndexType {
        FILE_TYPE,
        ID,
        UNIQUE,
        AUTOMATIC;
    }

    @Getter
    @AllArgsConstructor
    enum FileType {
        STRING(Arrays.asList(String.class), " varchar(255) "),
        BYTE(Arrays.asList(Byte.class, byte.class), " varbinary(1024) "),
        INTEGER(Arrays.asList(Integer.class, int.class), " int(11) "),
        LONG(Arrays.asList(Long.class, long.class), " bigint "),
        LOCAL_DATE_TIME(Arrays.asList(LocalDateTime.class, Timestamp.class), " datetime "),
        LOCAL_DATE(Arrays.asList(LocalDate.class, Date.class), " date "),
        DOUBLE(Arrays.asList(Double.class, double.class), " double "),
        FLOAT(Arrays.asList(Float.class, float.class), " float ");
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
    static enum JavaSchemaDataType {
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

        private Class clazz;
        private String alias;

        public static String getAlias(Class clazz) {
            for (JavaSchemaDataType value : values()) {
                if (value.getClazz() == clazz) {
                    return value.getAlias();
                }
            }
            return STRING.getAlias();
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