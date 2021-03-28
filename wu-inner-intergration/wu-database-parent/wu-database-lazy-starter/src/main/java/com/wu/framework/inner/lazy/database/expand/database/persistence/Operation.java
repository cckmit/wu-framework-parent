package com.wu.framework.inner.lazy.database.expand.database.persistence;

import java.util.List;

public interface Operation {

    <T> List<T> executeSQL(String sql, Class<T> t);

    /**
     * description 执行SQL 返回指定类型
     *
     * @param
     * @return
     * @exception/throws
     * @author Jiawei Wu
     * @date 2020/12/29 下午1:44
     */
    <T> T executeSQLForBean(String sql, Class<T> t);
}
