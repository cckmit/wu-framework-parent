package com.wu.framework.inner.lazy.hbase.expland.persistence.method;

import com.wu.framework.inner.layer.stereotype.LayerField;
import com.wu.framework.inner.layer.stereotype.analyze.AnalyzeField;
import com.wu.framework.inner.lazy.hbase.expland.analyze.HBaseLayerAnalyzeAdapter;
import com.wu.framework.inner.lazy.hbase.expland.persistence.stereotype.HBaseTable;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/3/28 10:12 下午
 */
public abstract class HBaseOperationMethodAbstractAdapter extends HBaseLayerAnalyzeAdapter implements HBaseOperationMethodAdapter<HBaseOperationMethodAdapter.HBaseExecuteParams> {


    /**
     * 是否存在唯一字段注解
     */
    private Map<Class, List<AnalyzeField>> UNIQUE_FIELD = new HashMap<>();




    @Override
    public Object before(HBaseExecuteParams o) throws Exception {
        return o;
    }

    @Override
    public Object run(HBaseExecuteParams hBaseExecuteParam) throws Exception {
        // 执行前处理操作
        perfectTable(hBaseExecuteParam.getConnection().getAdmin(), hBaseExecuteParam.getObjects()[0]);
        return execute(hBaseExecuteParam.getConnection(), hBaseExecuteParam.getObjects());
    }

    @Override
    public Object after(HBaseExecuteParams o) throws Exception {
        return o;
    }

    /**
     * @param
     * @return
     * @describe 是否完善表
     * @author Jia wei Wu
     * @date 2021/4/7 6:55 下午
     **/
    boolean perfectTable(Admin admin, Object source) throws IOException {
        Class clazz;
        if (source instanceof Collection) {
            clazz = ((Collection) source).iterator().next().getClass();
        } else if (source instanceof Class) {
            clazz = (Class) source;
        } else {
            clazz = source.getClass();
        }
        HBaseTable hBaseTable = analyzeClass(clazz);
        if (hBaseTable.perfectTable()) {

            NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(hBaseTable.namespace()).build();

            try {
                admin.createNamespace(namespaceDescriptor);
            } catch (Exception e) {
                System.out.println(namespaceDescriptor.getName() + "命名空间已存在!");
            }

            TableName tableName = TableName.valueOf(hBaseTable.namespace(), hBaseTable.tableName());
            TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tableName).
                    setColumnFamily(new ColumnFamilyDescriptorBuilder.ModifyableColumnFamilyDescriptor(Bytes.toBytes(hBaseTable.columnFamily())));
            TableDescriptor build = tableDescriptorBuilder.build();
            if (admin.tableExists(tableName)) {
                admin.modifyTable(build);
            } else {
                admin.createTable(build);
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * description 获取 hBaseRow
     *
     * @param
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/9 下午1:25
     */
    public String hBaseRow(List<AnalyzeField> analyzeFieldList, Object source) {
        String hBaseRow = UUID.randomUUID().toString();
        List<AnalyzeField> uniqueConvertedFieldList;
        if (UNIQUE_FIELD.containsKey(source.getClass())) {
            uniqueConvertedFieldList = UNIQUE_FIELD.get(source.getClass());
        } else {
            uniqueConvertedFieldList = analyzeFieldList.stream().
                    filter(convertedField -> LayerField.LayerFieldType.UNIQUE.equals(convertedField.getFieldIndexType())).
                    collect(Collectors.toList());
        }
        if (!ObjectUtils.isEmpty(uniqueConvertedFieldList)) {
            hBaseRow = uniqueConvertedFieldList.stream().map(convertedField -> {
                Field field = ReflectionUtils.findField(source.getClass(), convertedField.getFieldName());
                field.setAccessible(true);
                Object o = "";
                try {
                    o = field.get(source);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return o.toString();
            }).collect(Collectors.joining("-"));
        } else {
//            System.err.println(String.format("the uniqueness field cannot be found, and the current result cannot be updated in class %s ", source.getClass()));
        }
        return hBaseRow;
    }

}
