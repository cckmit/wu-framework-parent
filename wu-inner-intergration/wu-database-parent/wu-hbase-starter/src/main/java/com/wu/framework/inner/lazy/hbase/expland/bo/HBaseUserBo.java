package com.wu.framework.inner.lazy.hbase.expland.bo;

import com.wu.framework.inner.lazy.hbase.expland.persistence.stereotype.HBaseTable;
import com.wu.framework.inner.lazy.hbase.expland.persistence.stereotype.HBaseTableUnique;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/3/27 9:41 下午
 */
@Accessors(chain = true)
@Data
@HBaseTable(tableName = "java_hbase_user", columnFamily = "hbase_user_bo", perfectTable = true, namespace = "hbase_java")
public class HBaseUserBo {

    @HBaseTableUnique
    private int id;
    private String userName;
    private String age;
    private String sex;
    private LocalDate birthday = LocalDate.now();
}
