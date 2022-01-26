package com.wu.framework.inner.lazy.database.expand.database.persistence.analyze;

import com.wu.framework.inner.layer.CamelAndUnderLineConverter;
import com.wu.framework.inner.layer.stereotype.LayerField;
import com.wu.framework.inner.layer.stereotype.analyze.LayerAnalyzeAdapter;
import com.wu.framework.inner.layer.stereotype.analyze.LayerClassAnalyzeAdapter;
import com.wu.framework.inner.lazy.database.expand.database.persistence.conf.UpsertJsonMessage;
import com.wu.framework.inner.lazy.database.expand.database.persistence.domain.ConvertedField;
import com.wu.framework.inner.lazy.database.expand.database.persistence.domain.LazyTableAnnotation;
import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTable;
import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.wu.framework.inner.lazy.database.expand.database.persistence.analyze.EasyAnnotationConverter.annotationConvertConversion;

public interface SQLAnalyze extends LayerClassAnalyzeAdapter {

    Logger log = LoggerFactory.getLogger(SQLAnalyze.class);

    String AUTHOR = "wujiawei";
    /**
     * 打印建表语句
     *
     * @param clazz 数据传输类
     */
//    SQL 描述
    String SQL_DESC =
            "-- ——————————————————————————\n" +
                    "-- create table %s  %s  \n" +  // 表名  表描述
                    "-- add by  %s  %s  \n" +   // 作者  日期
                    "-- ——————————————————————————" + "\n";
    //    删表
    String SQL_DROP = "DROP TABLE IF EXISTS `%s`;\n";
    //    默认字段
    String SQL_DEFAULT_FIELD =
            "`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                    "`is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',\n" +
                    "`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                    "`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                    "PRIMARY KEY (`id`) USING BTREE\n";

    /**
     * 打印插入更新的mybatis语句
     *
     * @param clazz 数据传输类
     */
    default void upsertSQL(Class clazz) {
        StringBuilder stringBuilder = new StringBuilder("insert into ");
        List<String> fieldNames = new ArrayList<>();
        List<Integer> ignoredIndex = new ArrayList<>();
        // 添加表名
        stringBuilder.append(tableName(clazz));
        // 添加字段
        Field[] fields = clazz.getDeclaredFields();
        stringBuilder.append("(");
        for (int i = 0; i < fields.length; i++) {
            Field declaredField = fields[i];
            LazyTableField tableField = AnnotationUtils.getAnnotation(declaredField, LazyTableField.class);
            String fieldName = CamelAndUnderLineConverter.humpToLine2(declaredField.getName());
            if (!ObjectUtils.isEmpty(tableField)) {
                if (!tableField.exist()) {
                    ignoredIndex.add(i);
                    continue;
                }
                if (!ObjectUtils.isEmpty(tableField.value())) {
                    fieldName = tableField.value();
                }
            }
            fieldNames.add(fieldName);
//            stringBuilder.append(fieldName);
//            if (i != fields.length - 1) {
//                stringBuilder.append(",");
//            }
        }
        stringBuilder.append(String.join(",", fieldNames));
        stringBuilder.append(")  VALUES " + "\n" + "<foreach collection=\"dtoList\" item=\"dto\" separator=\",\"> \n (");
        // 添加 foreach
        for (int i = 0; i < fields.length; i++) {
            if (ignoredIndex.contains(i)) {
                continue;
            }
            stringBuilder.append("#{dto.").append(fields[i].getName()).append("}");
            if (i != fields.length - 1) {
                stringBuilder.append(",\n");
            }
        }
        stringBuilder.append(") \n </foreach> \n ON DUPLICATE KEY UPDATE \n");
        // 更新
        for (int i = 0; i < fieldNames.size(); i++) {
            if (ignoredIndex.contains(i)) {
                continue;
            }
            stringBuilder.append(fieldNames.get(i)).append("=VALUES (").append(fieldNames.get(i)).append(")");
            if (i != fieldNames.size() - 1) {
                stringBuilder.append(",\n");
            }
        }
        System.out.println("插入语句：\n" + stringBuilder);
    }



    /**
     * 获取 表名称
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/3 下午9:48
     **/
    static <T> String tableName(Class<T> clazz) {
        LazyTable tableNameAnnotation = AnnotatedElementUtils.findMergedAnnotation(clazz, LazyTable.class);
        if (!ObjectUtils.isEmpty(tableNameAnnotation) && !ObjectUtils.isEmpty(tableNameAnnotation.tableName())) {
            return tableNameAnnotation.tableName();
        } else {
            return CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
        }
    }

    /**
     * 转换成可执行的sql
     *
     * @param collection
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> String upsertPreparedStatementSQL(Collection collection, Class<T> clazz) {
        return upsertPreparedStatementSQL(collection, clazz, null);
    }

    /**
     * description 打印可以执行的sql脚本
     *
     * @param collection
     * @return String
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/9/17 下午1:21
     */
    static <T> String upsertPreparedStatementSQL(Collection collection, Class<T> clazz, Map iEnumList) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder stringBuilder = new StringBuilder("insert into ");
        List<String> fieldNames = new ArrayList<>();
        List<String> updateFieldNames = new ArrayList<>();
        // 添加表名
        stringBuilder.append(tableName(clazz));
        // 添加字段
        Field[] fields = clazz.getDeclaredFields();
        stringBuilder.append("(");
        /**
         * 是否添加逗号
         */
        for (Field declaredField : fields) {
            if (UpsertJsonMessage.ignoredFields.contains(declaredField.getName())) {
                continue;
            }
            LazyTableField tableField = AnnotatedElementUtils.findMergedAnnotation(declaredField, LazyTableField.class);
            String fieldName = CamelAndUnderLineConverter.humpToLine2(declaredField.getName());
            if (!ObjectUtils.isEmpty(tableField)) {
                if (!tableField.exist()) {
                    continue;
                }
                if (!ObjectUtils.isEmpty(tableField.value())) {
                    fieldName = tableField.value();
                }
            }
            fieldNames.add(fieldName);
            updateFieldNames.add(fieldName + "=VALUES (" + fieldName + ")");
        }

        stringBuilder.append(String.join(",", fieldNames));

        stringBuilder.append(")  VALUES " + "\n");
        // 添加 数据
        int collectionIndex = 0;
        for (Object o : collection) {
            List<Object> fieldValList = new ArrayList<>();
            collectionIndex++;
            stringBuilder.append("(");
            for (Field declaredField : o.getClass().getDeclaredFields()) {
                try {
                    declaredField.setAccessible(true);
                    if (UpsertJsonMessage.ignoredFields.contains(declaredField.getName())) {
                        continue;
                    }
                    Object fieldVal = declaredField.get(o);
                    LazyTableField lazyTableField = AnnotationUtils.getAnnotation(declaredField, LazyTableField.class);
                    if (!ObjectUtils.isEmpty(lazyTableField) && !lazyTableField.exist()) {
                        continue;
                    }
                    if (ObjectUtils.isEmpty(fieldVal)) {
                        if (null != lazyTableField && !ObjectUtils.isEmpty(lazyTableField.fieldDefaultValue())) {
                            fieldVal = lazyTableField.fieldDefaultValue();
                        } else {
                            fieldVal = "NULL";
                        }
                    } else {
                        // 时间格式处理
                        Object tempFieldVal = getDate(fieldVal, sf);
                        if (null == tempFieldVal) {
                            fieldVal = annotationConvertConversion(declaredField, fieldVal, iEnumList);
                            fieldVal = fieldVal.toString().replaceAll("'", "\"");
                        } else {
                            fieldVal = tempFieldVal;
                        }
                        fieldVal = "'" + fieldVal + "'";
                    }
                    fieldValList.add(fieldVal);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            stringBuilder.append(fieldValList.stream().map(o1 -> o1.toString()).collect(Collectors.joining(",")));
            stringBuilder.append(")");
            if (collectionIndex != collection.size()) {
                stringBuilder.append(",\n");
            }
            stringBuilder.append("\n");
        }
        // 更新
        stringBuilder.append(" ON DUPLICATE KEY UPDATE \n");
        stringBuilder.append(updateFieldNames.stream().collect(Collectors.joining(",")));
        return stringBuilder.toString();
    }

    /**
     * description 获取时间格式
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/9/17 下午2:31
     */
    static String getDate(Object o, SimpleDateFormat sf) {
        if (Date.class.equals(o.getClass())) {
            return sf.format((Date) o);
        }
        if (LocalDateTime.class.equals(o.getClass())) {
            return ((LocalDateTime) o).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return null;
    }


    /**
     * description 扫描包下面所有class
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/7/13 下午6:40
     */
    static List<Class> scanClass(String classPath) {
        return scanClass(classPath, null);
    }


    static List<Class> scanClass(String classPath, Class<? extends Annotation> annotation) {
        List<Class> classList = new ArrayList<>();
        if (ObjectUtils.isEmpty(classPath)) {
            return classList;
        }
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        TypeFilter includeFilter = (metadataReader, metadataReaderFactory) -> true;
        provider.addIncludeFilter(includeFilter);
        Set<BeanDefinition> beanDefinitionSet = new HashSet<>();
        // 指定扫描的包名
        Set<BeanDefinition> candidateComponents = provider.findCandidateComponents(classPath);
        beanDefinitionSet.addAll(candidateComponents);

        beanDefinitionSet.forEach(beanDefinition -> {
            GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinition;
            try {
                Class clazz = Class.forName(beanDefinition.getBeanClassName());

                if (!ObjectUtils.isEmpty(annotation)) {
                    if (!ObjectUtils.isEmpty(AnnotationUtils.getAnnotation(clazz, annotation))) {
                        classList.add(clazz);
                    }
                } else {
                    classList.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
//            System.out.println(definition.getBeanClassName());
        });
        return classList;
    }

    static void clearConsole() {
        try {
            String os = System.getProperty("os.tableName");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception exception) {
            //  Handle exception.
        }
    }


    /**
     * description 创建sql查询语句
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/7/8 下午1:06
     */
    default String createSelectSQL(Class clazz) {
        /**
         *     <sql id="SEARCH_CONDITION_SQL">
         *         <where>
         *             <if test="condition.netName!=null and condition.netName !=''">
         *                 AND T.NET_NAME LIKE CONCAT('%',#{condition.netName,jdbcType=VARCHAR},'%')
         *             </if>
         *             <if test="condition.status !=null and condition.status !=''">
         *                 AND T.STATUS = #{condition.status,jdbcType=VARCHAR}
         *             </if>
         *             <if test="condition.rentCountUp !=null and condition.rentCountUp !=''">
         *                 AND T.RENT_COUNT > #{condition.rentCountUp,jdbcType=NUMERIC}
         *             </if>
         *             <if test="condition.rentCountDown !=null and condition.rentCountDown !=''">
         *                 AND T.RENT_COUNT &lt; #{condition.rentCountDown,jdbcType=NUMERIC}
         *             </if>
         *             <if test="condition.restoreCountUp !=null and condition.restoreCountUp !=''">
         *                 AND T.RESTORE_COUNT > #{condition.restoreCountUp,jdbcType=NUMERIC}
         *             </if>
         *             <if test="condition.restoreCountDown !=null and condition.restoreCountDown !=''">
         *                 AND T.RESTORE_COUNT &lt; #{condition.restoreCountDown,jdbcType=NUMERIC}
         *             </if>
         *         </where>
         *     </sql>
         *
         *
         *         <select id="selectPageBikeStation" resultType="BikeStationDto">
         *         SELECT T.*
         *         FROM
         *         ct_pub_bike_infr_station_capcon_bas T
         *         <include refid="SEARCH_CONDITION_SQL"/>
         *     </select>
         *
         */
        StringBuilder builder = new StringBuilder("  <sql id=\"SEARCH_CONDITION_SQL\"> \n <where> \n");

        // 条件
        List<ConvertedField> convertedFieldList = fieldNamesOnAnnotation(clazz, null);
        for (ConvertedField convertedField : convertedFieldList) {
            String convertedFieldName = convertedField.getConvertedFieldName();
            String fieldName = convertedField.getFieldName();
            builder.append("<if test=\"condition." + fieldName + "!=null and condition." + fieldName + " !=''\"> \n");
//            if (convertedField.getClazz().equals(String.class)) {
//                // 包含    AND T.address LIKE CONCAT('%',#{condition.address}, '%')
//                builder.append(" AND T." + convertedFieldName + " LIKE CONCAT('%',#{condition." + fieldName + "}, '%') \n");
//            } else {
            builder.append(" AND T." + convertedFieldName + " = #{condition." + fieldName + "} \n");
//            }
            builder.append("</if> \n");
        }
        builder.append("  </where> \n </sql>\n");
        // 查询sql
        String tableName = tableName(clazz);
        builder.append(" <select id=\"select" + clazz.getSimpleName() + "\" resultType=\"" + clazz.getName() + "\"> \n");
        builder.append("SELECT T.* FROM ");
        builder.append(tableName).append(" T \n");
        builder.append("<include refid=\"SEARCH_CONDITION_SQL\"/> \n </select>\n");
        System.out.println(builder.toString());
        return builder.toString();
    }


    /**
     * description 不完整创建sql查询语句
     *
     * @param
     * @return
     * @exception/throws
     * @author Jiawei Wu
     * @date 2020/12/29 下午12:39
     */
    static String createSelectSQL(List<ConvertedField> convertedFieldList, String tableName) {
        /**
         *     <sql id="SEARCH_CONDITION_SQL">
         *         <where>
         *             <if test="condition.netName!=null and condition.netName !=''">
         *                 AND T.NET_NAME LIKE CONCAT('%',#{condition.netName,jdbcType=VARCHAR},'%')
         *             </if>
         *             <if test="condition.status !=null and condition.status !=''">
         *                 AND T.STATUS = #{condition.status,jdbcType=VARCHAR}
         *             </if>
         *             <if test="condition.rentCountUp !=null and condition.rentCountUp !=''">
         *                 AND T.RENT_COUNT > #{condition.rentCountUp,jdbcType=NUMERIC}
         *             </if>
         *             <if test="condition.rentCountDown !=null and condition.rentCountDown !=''">
         *                 AND T.RENT_COUNT &lt; #{condition.rentCountDown,jdbcType=NUMERIC}
         *             </if>
         *             <if test="condition.restoreCountUp !=null and condition.restoreCountUp !=''">
         *                 AND T.RESTORE_COUNT > #{condition.restoreCountUp,jdbcType=NUMERIC}
         *             </if>
         *             <if test="condition.restoreCountDown !=null and condition.restoreCountDown !=''">
         *                 AND T.RESTORE_COUNT &lt; #{condition.restoreCountDown,jdbcType=NUMERIC}
         *             </if>
         *         </where>
         *     </sql>
         *
         *
         *         <select id="selectPageBikeStation" resultType="BikeStationDto">
         *         SELECT T.*
         *         FROM
         *         ct_pub_bike_infr_station_capcon_bas T
         *         <include refid="SEARCH_CONDITION_SQL"/>
         *     </select>
         *
         */
        StringBuilder builder = new StringBuilder("  <sql id=\"SEARCH_CONDITION_SQL\"> \n <where> \n");
        // 条件
        for (ConvertedField convertedField : convertedFieldList) {
            String convertedFieldName = convertedField.getConvertedFieldName();
            String fieldName = convertedField.getFieldName();
            builder.append("<if test=\"condition." + fieldName + "!=null and condition." + fieldName + " !=''\"> \n");
//            if (convertedField.getClazz().equals(String.class)) {
//                // 包含    AND T.address LIKE CONCAT('%',#{condition.address}, '%')
//                builder.append(" AND T." + convertedFieldName + " LIKE CONCAT('%',#{condition." + fieldName + "}, '%') \n");
//            } else {
            builder.append(" AND T." + convertedFieldName + " = #{condition." + fieldName + "} \n");
//            }
            builder.append("</if> \n");
        }
        builder.append("  </where> \n </sql>\n");
        // 查询sql
        String beanName = CamelAndUnderLineConverter.lineToHump(tableName);
        builder.append(" <select id=\"select" + beanName + "\" resultType=\"" + beanName + "\"> \n");
        builder.append("SELECT T.* FROM ");
        builder.append(tableName).append(" T \n");
        builder.append("<include refid=\"SEARCH_CONDITION_SQL\"/> \n </select>\n");
        System.out.println(builder.toString());
        return builder.toString();
    }

    /**
     * description  抽取 字段名和需要映射属性名
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/9/17 下午1:29
     */
    default  <T> List<ConvertedField> fieldNamesOnAnnotation(Class<T> clazz, LayerField.LayerFieldType tableFileIndexType) {
        List<ConvertedField> convertedFieldList = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (!declaredField.isAccessible()) {
                declaredField.setAccessible(true);
            }
            LazyTableField lazyTableField = AnnotatedElementUtils.findMergedAnnotation(declaredField, LazyTableField.class);
            String convertedFieldName = CamelAndUnderLineConverter.humpToLine2(declaredField.getName());
            ConvertedField convertedField = new ConvertedField();
            if (!ObjectUtils.isEmpty(lazyTableField)) {
                if (!lazyTableField.exist()) {
                    continue;
                }
                // 判断是否是我想要的类型
                if (!ObjectUtils.isEmpty(tableFileIndexType) && !tableFileIndexType.equals(lazyTableField.indexType())) {
                    continue;
                }
                if (!ObjectUtils.isEmpty(lazyTableField.value())) {
                    convertedFieldName = lazyTableField.value();
                }
                convertedField.setComment(lazyTableField.comment());
                convertedField.setFieldIndexType(lazyTableField.indexType());
            }
            convertedField.setConvertedFieldName(convertedFieldName);
            convertedField.setFieldName(declaredField.getName());
            convertedField.setClazz(declaredField.getType());
            convertedField.setType(LazyTableField.FileType.getTypeByClass(declaredField.getType()));
            convertedFieldList.add(convertedField);
        }
        return convertedFieldList;
    }


    Map<Class, LazyTableAnnotation> CLASS_CUSTOM_TABLE_ANNOTATION_ATTR_MAP = new ConcurrentHashMap<>();


    /**
     * @param
     * @return
     * @describe 通过class 解析出 LazyTableAnnotation 对象
     * @author Jia wei Wu
     * @date 2021/4/11 10:47 上午
     **/
    default LazyTableAnnotation classLazyTableAnalyze(Class clazz) {
        if (!CLASS_CUSTOM_TABLE_ANNOTATION_ATTR_MAP.containsKey(clazz)) {
            LazyTable lazyTable = AnnotatedElementUtils.findMergedAnnotation(clazz, LazyTable.class);
            String className = clazz.getName();
            String tableName = analyze(clazz).name();
            String comment = "";
            boolean smartFillField = false;
            LazyTableAnnotation lazyTableAnnotation = new LazyTableAnnotation();
            if (null != lazyTable) {
                lazyTableAnnotation.setSchema(lazyTable.schema());
                smartFillField = lazyTable.smartFillField();
            }
            lazyTableAnnotation.setComment(comment);
            lazyTableAnnotation.setClassName(className);
            lazyTableAnnotation.setClazz(clazz);
            lazyTableAnnotation.setTableName(tableName);
            lazyTableAnnotation.setConvertedFieldList(fieldNamesOnAnnotation(clazz, null));
            lazyTableAnnotation.setSmartFillField(smartFillField);
            log.info("Initialize {} annotation parameters  className:[{}],tableName:[{}],comment:[{}]", clazz,
                    className, tableName, comment);
            CLASS_CUSTOM_TABLE_ANNOTATION_ATTR_MAP.put(clazz, lazyTableAnnotation);
        }
        return CLASS_CUSTOM_TABLE_ANNOTATION_ATTR_MAP.get(clazz);
    }

    /**
     * 更新的sql
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/5 下午2:11
     **/
    default String updatePreparedStatementSQL(Object o) {
        // update
        StringBuffer stringBuffer = new StringBuffer("update ");
        // table
        stringBuffer.append(tableName(o.getClass())).append(" set ");
        List<ConvertedField> convertedFieldList = fieldNamesOnAnnotation(o.getClass(),null);
        boolean punctuationFlag = false;
        boolean andFlag = false;
        StringBuffer stringBufferWhere = new StringBuffer(" where ");
        for (ConvertedField convertedField : convertedFieldList) {
            try {

                Field field = o.getClass().getDeclaredField(convertedField.getFieldName());
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object fieldVal = field.get(o);
                if (convertedField.getFieldIndexType().equals(LayerField.LayerFieldType.FILE_TYPE)) {
                    if (punctuationFlag) {
                        stringBuffer.append(",");
                    }
                    stringBuffer.append(convertedField.getConvertedFieldName()).append(" = '").append(fieldVal).append("'");
                    punctuationFlag = true;
                } else {
                    if (ObjectUtils.isEmpty(fieldVal)) {
                        continue;
                    }
                    if (andFlag) {
                        stringBufferWhere.append(" and ");
                    }
                    stringBufferWhere.append(convertedField.getConvertedFieldName()).append(" = '").append(fieldVal).append("'");
                    andFlag = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // where
        stringBuffer.append(stringBufferWhere);
        System.err.println(stringBuffer.toString());
        return stringBuffer.toString();

    }

    /**
     * 删除sql
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/7/5 下午4:00
     **/
    default  <T> String deletePreparedStatementSQL(Object o) {
        Class clazz = o.getClass();
        // DELETE FROM
        StringBuffer stringBuffer = new StringBuffer(" DELETE FROM  ");
        stringBuffer.append(tableName(clazz));
        // where
        stringBuffer.append(" where ");
        boolean punctuationFlag = false;
        List<ConvertedField> convertedFieldList = fieldNamesOnAnnotation(clazz, LayerField.LayerFieldType.ID);

        for (ConvertedField convertedField : convertedFieldList) {
            try {
                Field field = o.getClass().getDeclaredField(convertedField.getFieldName());
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object fieldVal = field.get(o);
                if (ObjectUtils.isEmpty(fieldVal)) {
                    throw new IllegalArgumentException("根据id主键字段删除 不能为空");
                }
                // add data
                if (punctuationFlag) {
                    stringBuffer.append(" and ");
                }
                stringBuffer.append(convertedField.getConvertedFieldName()).append(" = '").append(fieldVal).append("'");
                punctuationFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return stringBuffer.toString();
    }

}
