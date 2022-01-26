package com.wu.framework.inner.lazy.hbase.expland.persistence.method;


import com.wu.framework.inner.layer.stereotype.analyze.AnalyzeField;
import com.wu.framework.inner.lazy.hbase.expland.persistence.stereotype.HBaseTable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/3/29 7:19 下午
 */
@Deprecated
public class HBaseOperationInsertMethodAdapter extends HBaseOperationMethodAbstractAdapter {


    @Override
    public Object execute(Connection connection, Object... args) throws Exception {
        Object entity = args[0];
        HBaseTable hBaseTable = analyzeClass(entity.getClass());
        Table table = connection.getTable(TableName.valueOf(hBaseTable.namespace(), hBaseTable.tableName()));

        List<AnalyzeField> analyzeFieldList = analyzeField(entity.getClass());
        Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        for (AnalyzeField analyzeField : analyzeFieldList) {
            Field field = ReflectionUtils.findField(entity.getClass(), analyzeField.getFieldName());
            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            put.addColumn(Bytes.toBytes(hBaseTable.columnFamily()), Bytes.toBytes(analyzeField.getConvertedFieldName()),
                    Bytes.toBytes(ObjectUtils.isEmpty(fieldValue) ? "" : fieldValue.toString()));
        }
        table.put(put);
        return null;
    }

}
