package com.wu.framework.inner.lazy.hbase.expland.persistence.method;


import com.wu.framework.inner.layer.stereotype.analyze.AnalyzeField;
import com.wu.framework.inner.lazy.hbase.expland.persistence.stereotype.HBaseTable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/3/29 7:19 下午
 */
@Component
public class HBaseOperationUpsertListMethodAdapter extends HBaseOperationMethodAbstractAdapter {


    @Override
    public Object execute(Connection connection, Object... args) throws Exception {

        Object entity = args[0];

        Class entityClass;
        Table table;
        if (entity instanceof List) {
            entityClass = ((List) entity).get(0).getClass();
            // 处理数据
            HBaseTable hBaseTable = analyzeClass(entityClass);
            table = connection.getTable(TableName.valueOf(hBaseTable.namespace(),hBaseTable.tableName()));

            List<AnalyzeField> analyzeFieldList = analyzeField(entityClass);

            List<Put> putList = new ArrayList<>();
            for (Object o : ((List) entity)) {
                Put put = new Put(Bytes.toBytes(hBaseRow(analyzeFieldList, o)));
                for (AnalyzeField analyzeField : analyzeFieldList) {
                    Field field = ReflectionUtils.findField(entityClass, analyzeField.getFieldName());
                    assert field != null;
                    field.setAccessible(true);
                    Object fieldValue = field.get(o);
                    put.addColumn(Bytes.toBytes(hBaseTable.columnFamily()), Bytes.toBytes(analyzeField.getConvertedFieldName()),
                            Bytes.toBytes(ObjectUtils.isEmpty(fieldValue) ? "" : fieldValue.toString()));
                }
                putList.add(put);
            }
            table.put(putList);

        } else {
            entityClass = entity.getClass();
            // 处理数据
            HBaseTable hBaseTable = analyzeClass(entityClass);
            table = connection.getTable(TableName.valueOf(hBaseTable.namespace(),hBaseTable.tableName()));

            List<AnalyzeField> analyzeFieldList = analyzeField(entityClass);

            Put put = new Put(Bytes.toBytes(hBaseRow(analyzeFieldList, entity)));
            for (AnalyzeField analyzeField : analyzeFieldList) {
                Field field = ReflectionUtils.findField(entityClass, analyzeField.getFieldName());
                field.setAccessible(true);
                Object fieldValue = field.get(entity);
                put.addColumn(Bytes.toBytes(hBaseTable.columnFamily()), Bytes.toBytes(analyzeField.getConvertedFieldName()),
                        Bytes.toBytes(ObjectUtils.isEmpty(fieldValue) ? "" : fieldValue.toString()));
            }
            table.put(put);
        }
        table.close();
        return true;

    }
}
