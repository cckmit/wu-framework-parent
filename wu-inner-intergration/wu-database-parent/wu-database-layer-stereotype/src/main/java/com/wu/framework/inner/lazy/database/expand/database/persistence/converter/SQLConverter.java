package com.wu.framework.inner.lazy.database.expand.database.persistence.converter;

import com.wu.framework.inner.lazy.database.expand.database.persistence.analyze.SQLAnalyze;

/**
 * @author : Jia wei Wu
 * @version 1.0
 * @describe :
 * @date : 2021/4/18 11:21 上午
 */
public class SQLConverter {
    public final static SQLAnalyze sqlAnalyze = new SQLAnalyze() {
    };


    /**
     * description 创建建表语句
     * @param clazz 类
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/19 下午2:45
     */
    public static String creatTableSQL(Class clazz) {
        return sqlAnalyze.classLazyTableAnalyze(clazz).creatTableSQL();
    }


    /**
     * description 创建查询语句
     * @param clazz 类
     * @return
     * @exception/throws
     * @author Jia wei Wu
     * @date 2021/4/19 下午2:44
     */
    public static String createSelectSQL(Class clazz) {
        return sqlAnalyze.createSelectSQL(clazz);
    }
}
