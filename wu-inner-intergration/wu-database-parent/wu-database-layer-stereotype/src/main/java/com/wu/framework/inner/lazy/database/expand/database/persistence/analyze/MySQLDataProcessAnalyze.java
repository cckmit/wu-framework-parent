package com.wu.framework.inner.lazy.database.expand.database.persistence.analyze;

import com.wu.framework.inner.lazy.database.expand.database.persistence.domain.ConvertedField;
import com.wu.framework.inner.lazy.database.expand.database.persistence.map.EasyHashMap;
import com.wu.framework.inner.lazy.database.expand.database.persistence.conf.UpsertJsonMessage;
import com.wu.framework.inner.lazy.database.expand.database.persistence.domain.LazyTableAnnotation;
import com.wu.framework.inner.layer.data.JavaBasicType;
import com.wu.framework.inner.layer.data.IBeanUpsert;
import com.wu.framework.inner.layer.data.NormalUsedString;
import com.wu.framework.inner.layer.stereotype.LayerDefault;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wu.framework.inner.lazy.database.expand.database.persistence.analyze.EasyAnnotationConverter.annotationConvertConversion;

/**
 * description MySQL 数据预处理解析
 *
 * @author Jia wei Wu
 * @date 2020/10/22 下午2:25
 */
public interface MySQLDataProcessAnalyze extends LayerDefault,SQLAnalyze{



    /**
     * description 类解析
     *
     * @param clazz
     * @return Object
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/10/22 下午2:20
     */
    default LazyTableAnnotation classAnalyze(Class clazz) {
        return classLazyTableAnalyze(clazz );
    }




    /**
     * @return
     * @params 数据解析
     * @author Jiawei Wu
     * @date 2020/12/31 10:56 下午
     **/
    default LazyTableAnnotation dataAnalyze(Class clazz, EasyHashMap easyHashMap) {
        LazyTableAnnotation lazyTableAnnotation;
        if (EasyHashMap.class.isAssignableFrom(clazz)) {
            lazyTableAnnotation = easyHashMap.toEasyTableAnnotation();
        } else {
            lazyTableAnnotation = classAnalyze(clazz);
        }
        return lazyTableAnnotation;
    }

    /**
     * description upsert数据包装
     * INSERT INTO TEMP_EASY_HASH (DATE,NAME,TYPE) VALUES ('2021-04-18','MAP','EASY')  ON DUPLICATE KEY UPDATE
     *  DATE=VALUES (DATE),NAME=VALUES (NAME),TYPE=VALUES (TYPE)
     *
     * @param sourceData 源数据
     * @return ProcessResult
     * @exception/throws
     * @author Jia wei Wu
     * @date 2020/10/22 下午2:23
     */
    default MySQLProcessResult upsertDataPack(List sourceData, LazyTableAnnotation lazyTableAnnotation) throws Exception {
        MySQLProcessResult mySQLProcessResult = new MySQLProcessResult();
        List<InputStream> binaryList = new ArrayList<>();
        String insert = "insert into %s (%s) VALUES %s  ON DUPLICATE KEY UPDATE \n %s ";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder stringBuilder = new StringBuilder("insert into ");
        // 添加表名
        stringBuilder.append(lazyTableAnnotation.getTableName());
        // 添加字段
        List<ConvertedField> convertedFieldList = lazyTableAnnotation.getConvertedFieldList();
        String column = convertedFieldList.stream().filter(ConvertedField::isExist).
                filter(convertedField -> !UpsertJsonMessage.ignoredFields.contains(convertedField.getFieldName())).
                map(ConvertedField::getConvertedFieldName).
                collect(Collectors.joining(","));
        String updateColumn = convertedFieldList.stream().filter(ConvertedField::isExist).
                filter(convertedField -> !UpsertJsonMessage.ignoredFields.contains(convertedField.getFieldName())).
                map(convertedField -> convertedField.getConvertedFieldName() + "=VALUES (" + convertedField.getConvertedFieldName() + ")").
                collect(Collectors.joining(","));

        String data;
        // 添加 数据
        if (lazyTableAnnotation.getClassName().equals(EasyHashMap.class.getName())) {
            data = ((List<EasyHashMap>) sourceData).stream().
                    map(easyHashMap -> NormalUsedString.LEFT_BRACKET + convertedFieldList.stream().map(convertedField -> {
                        try {
                            easyHashMap.beforeObjectProcess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final Object value = easyHashMap.getOrDefault(convertedField.getFieldName(), null);
                        //判断是否为binary数据
                        final InputStream binary = isBinary(value);
                        if (!ObjectUtils.isEmpty(binary)) {
                            binaryList.add(binary);
                            return NormalUsedString.QUESTION_MARK;
                        }
                        if (value == null && !JavaBasicType.DEFAULT_VALUE_HASHMAP.containsKey(convertedField.getClazz())) {
                            throw new RuntimeException("current  data is null and we could not find the default value of type" + convertedField.getClazz());
                        }
                        return "'" + (value == null ? JavaBasicType.DEFAULT_VALUE_HASHMAP.get(convertedField.getClazz()) : value).toString().replaceAll("'", "’") + "'";
                    }).collect(Collectors.joining(",")) + NormalUsedString.RIGHT_BRACKET).collect(Collectors.joining(","));
        } else {
            final List<Field> fieldList = convertedFieldList.stream().filter(ConvertedField::isExist).
                    filter(convertedField -> !UpsertJsonMessage.ignoredFields.contains(convertedField.getFieldName())).
                    map(convertedField -> ReflectionUtils.findField(lazyTableAnnotation.getClazz(), convertedField.getFieldName())).
                    peek(field -> field.setAccessible(true)).
                    collect(Collectors.toList());
            List<String> tempList = new ArrayList<>();
            for (Object source : sourceData) {
                if (IBeanUpsert.class.isAssignableFrom(lazyTableAnnotation.getClazz())) {
                    ((IBeanUpsert) source).beforeObjectProcess();
                }
                String temp = NormalUsedString.LEFT_BRACKET +
                        fieldList.stream().map(field -> {
                            Object fieldVal = "";
                            try {
                                fieldVal = field.get(source);
                                //判断是否为binary数据
                                final InputStream binary = isBinary(fieldVal);
                                if (!ObjectUtils.isEmpty(binary)) {
                                    binaryList.add(binary);
                                    return NormalUsedString.QUESTION_MARK;
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            if (null != fieldVal) {
                                fieldVal = annotationConvertConversion(field, fieldVal, lazyTableAnnotation.getIEnumList());
                                return "'" + fieldVal.toString().replaceAll("'", "\"") + "'";
                            } else {
//                                fieldVal = JavaBasicType.DEFAULT_VALUE_HASHMAP.get(field.getType());
                                return null;
                            }
                        }).collect(Collectors.joining(NormalUsedString.COMMA)) + NormalUsedString.RIGHT_BRACKET;
                tempList.add(temp);
            }
            data = String.join(NormalUsedString.COMMA, tempList);
        }
        String sql = String.format(insert, lazyTableAnnotation.getTableName(), column, data, updateColumn);
        if (ObjectUtils.isEmpty(binaryList)) {
            mySQLProcessResult.setHasBinary(false);
        } else {
            mySQLProcessResult.setHasBinary(true);
        }
        mySQLProcessResult.setSql(sql);
        mySQLProcessResult.setBinaryList(binaryList);
        return mySQLProcessResult;
    }


    /**
     * @return
     * @params 完善表
     * @author Jiawei Wu
     * @date 2020/12/31 8:18 下午
     **/
    default int perfectTable(LazyTableAnnotation lazyTableAnnotation, DataSource dataSource) throws Exception {
        String tableName = lazyTableAnnotation.getTableName();
        Connection connection = null;
        int updateColumn = 0;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();

//            String catalog = dataSource.getConnection().getCatalog();

            ResultSet resultSet = metaData.getTables(lazyTableAnnotation.schema(), null, tableName, new String[]{"TABLE"});
            //
            String perfectTableSQL;
//            while(resultSet.next()){
//                String tableName1 = resultSet.getString("TABLE_NAME");
//                System.out.println("tablename:"+tableName1);
//            }

            if (!resultSet.next()) {
                String createTableSQL = lazyTableAnnotation.creatTableSQL();
                Statement statement = connection.createStatement();
                for (String s : createTableSQL.split(";")) {
                    statement.addBatch(s);
                }
                //执行建表语句
                int[] executeBatch = statement.executeBatch();
                statement.close();
                updateColumn = executeBatch.length;
                perfectTableSQL = createTableSQL;
                log.info("create table {} success",tableName);
            } else {
                String string = resultSet.getString(1);
                ResultSet columns = metaData.getColumns(lazyTableAnnotation.schema(), "%", tableName, "%");
                List<ConvertedField> currentColumnNameList = new ArrayList<>();
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int dataSize = columns.getInt("COLUMN_SIZE");
                    int digits = columns.getInt("DECIMAL_DIGITS");
                    int nullable = columns.getInt("NULLABLE");
                    ConvertedField convertedField = new ConvertedField();
                    convertedField.setConvertedFieldName(columnName).setType(columnType);
                    currentColumnNameList.add(convertedField);
//                            System.out.println(columnName + " " + columnType + " " + datasize + " " + digits + " " + nullable);
                }
                String alterTableSQL = lazyTableAnnotation.alterTableSQL(currentColumnNameList);
                if (!ObjectUtils.isEmpty(alterTableSQL)) {
                    Statement statement = connection.createStatement();
                    updateColumn = statement.executeUpdate(alterTableSQL);
                    statement.close();
                }
                perfectTableSQL = alterTableSQL;

            }
            //
//            if (!EasyUpsertLog.class.isAssignableFrom(lazyTableAnnotation.getClazz())) {
//                // 记录日志
//                EasyUpsertLog easyUpsertLog = new EasyUpsertLog();
//                easyUpsertLog.setContext(perfectTableSQL);
//                easyUpsertLog.setType(EasyUpsertType.MySQL.name());
//                LazyTableAnnotation logEasyTableAnnotation = LocalStorageClassAnnotation.getEasyTableAnnotation(EasyUpsertLog.class, true);
//                Statement logStatement = connection.createStatement();
//                logStatement.execute(upsertDataPack(Collections.singletonList(easyUpsertLog), logEasyTableAnnotation));
//            }
            return updateColumn;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            assert connection != null;
            connection.close();
        }

    }

    /**
     * @param
     * @return
     * @describe 字段是否为二进制数据
     * @author Jia wei Wu
     * @date 2021/3/1 7:07 下午
     **/
    @SneakyThrows
    default InputStream isBinary(Object fieldValue) {
        if (ObjectUtils.isEmpty(fieldValue)) return null;
        if (File.class.equals(fieldValue.getClass())) {
            return new FileInputStream((File) fieldValue);
        } else if (InputStream.class.isAssignableFrom(fieldValue.getClass())) {
            return (InputStream) fieldValue;
        } else if (InputStreamSource.class.isAssignableFrom(fieldValue.getClass())) {
            return ((InputStreamSource) fieldValue).getInputStream();
        }
        return null;
    }

    @Accessors(chain = true)
    @Data
    class MySQLProcessResult implements DataProcess.ProcessResult {
        private String sql;
        private List<InputStream> binaryList;

        private boolean hasBinary = false;

    }
}
