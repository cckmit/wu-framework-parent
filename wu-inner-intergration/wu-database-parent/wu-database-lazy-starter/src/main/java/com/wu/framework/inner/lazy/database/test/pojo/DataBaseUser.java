package com.wu.framework.inner.lazy.database.test.pojo;


import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTable;
import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTableField;
import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTableFieldId;
import com.wu.framework.inner.lazy.database.expand.database.persistence.stereotype.LazyTableFieldUnique;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(chain = true)
@Data
@LazyTable(tableName = "user", comment = "用户信息表")
@AllArgsConstructor
@NoArgsConstructor
public class DataBaseUser implements Serializable {
    //
    @LazyTableFieldId(value = "id")
    private Integer id;

    //    @EasySmartField(methodName = "username",comment = "username")
    @LazyTableFieldUnique(value = "username")
    private String username;

    @LazyTableField(value = "birthday")
    private String birthday;

    @LazyTableField("sex")
    private String sex;


    @LazyTableField(value = "address", exist = false)
    private String address;

    @LazyTableFieldUnique(value = "age")
    private Integer age;
}
