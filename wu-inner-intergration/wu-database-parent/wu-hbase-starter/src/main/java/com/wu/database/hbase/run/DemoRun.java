package com.wu.database.hbase.run;

import com.wu.framework.inner.layer.web.EasyController;
import com.wu.framework.inner.lazy.hbase.expland.bo.HBaseUserBo;
import com.wu.framework.inner.lazy.hbase.expland.persistence.HBaseOperation;
import com.wu.framework.inner.lazy.hbase.expland.persistence.proxy.HBaseOperationProxy;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/3/15 8:25 下午
 */
@EasyController
public class DemoRun {

    private final Connection connection;
    private final HBaseOperation hBaseOperation;


    public DemoRun(Connection connection, HBaseOperation hBaseOperation) {
        this.connection = connection;
        this.hBaseOperation = hBaseOperation;
    }


    @PostConstruct
    public void run() throws Exception {

//        boolean user = admin.tableExists(TableName.valueOf("hbase_user"));
        List<HBaseUserBo> hBaseUserBoList = new ArrayList<>();
        long a = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            hBaseUserBoList.add(new HBaseUserBo().setUserName("hbase_user").setAge("12").setSex("男").setId(i));
        }
        long b = System.currentTimeMillis();
        System.out.println("创建数据使用时间" + (b - a));
        hBaseOperation.insertList(hBaseUserBoList);
        b = System.currentTimeMillis();
        System.out.println("跑的真快" + (b - a));
//        HTableDescriptor[] hTableDescriptors = admin.listTables();
//        HTableDescriptor desc = new HTableDescriptor("easy1");
//        desc.addFamily(new HColumnDescriptor("cf1"));
//        admin.createTable(desc);
//        Table easy = connection.getTable(TableName.valueOf("easy12"));
//        System.out.println(admin.tableExists(TableName.valueOf("easy")));
//        admin.disableTable(TableName.valueOf("hbase_user"));
//        admin.deleteTable(TableName.valueOf("hbase_user"));
//        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf("hbase_user"));

//        hTableDescriptor.addFamily(new HColumnDescriptor("columnFamily"));
//        hTableDescriptor.addFamily(new HColumnDescriptor("A2"));
//        hTableDescriptor.addFamily(new HColumnDescriptor("A3"));
//        admin.createTable(hTableDescriptor);


//        final List<TableDescriptor> tableDescriptors = admin.listTableDescriptors();
//        for (TableDescriptor tableDescriptor : tableDescriptors) {
//            System.out.println(String.format("表%s数据", tableDescriptor.getTableName().getNameAsString()));
//            System.out.println(scanAllRecord(tableDescriptor.getTableName().getNameAsString()));
//        }
    }

    public String scanAllRecord(String tableName) throws IOException {
        String record = "";
        TableName name = TableName.valueOf(tableName);
        Table table = connection.getTable(name);
        Scan scan = new Scan();
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                for (Cell cell : result.rawCells()) {
                    StringBuffer stringBuffer = new StringBuffer()
                            .append(Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength())).append("\t")
                            .append(Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength())).append("\t")
                            .append(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())).append("\t")
                            .append(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())).append("\n");
                    String str = stringBuffer.toString();
                    record += str;
                }
            }
        }

        return record;
    }

}