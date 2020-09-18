package com.wu.framework.easy.stereotype.upsert.component;

import com.wu.framework.easy.stereotype.upsert.dynamic.EasyUpsertStrategy;
import com.wu.framework.easy.stereotype.upsert.enums.EasyUpsertType;
import com.wu.framework.easy.stereotype.upsert.ienum.UserDictionaryService;
import com.wu.framework.easy.stereotype.upsert.IEasyUpsert;
import com.wu.framework.easy.stereotype.upsert.config.UpsertConfig;
import com.wu.framework.easy.stereotype.upsert.converter.CustomAnnotationConverter;
import com.wu.framework.easy.stereotype.upsert.converter.SQLConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * description MySQL数据插入
 *
 * @author 吴佳伟
 * @date 2020/9/11 上午10:22
 */
@Slf4j
@EasyUpsertStrategy(value = EasyUpsertType.MySQL)
public abstract class MySQLEasyUpsert implements IEasyUpsert, InitializingBean {


    private final UserDictionaryService userDictionaryService;
    private final UpsertConfig upsertConfig;

    public MySQLEasyUpsert(UserDictionaryService userDictionaryService, UpsertConfig upsertConfig) {
        this.userDictionaryService = userDictionaryService;
        this.upsertConfig = upsertConfig;
    }

    protected abstract DataSource determineDataSource();


    @Override
    public <T> Object upsert(List<T> list) throws Exception {
        DataSource dataSource = determineDataSource();
        String threadName = Thread.currentThread().getName();
        if (list.size() > upsertConfig.getBatchLimit()) {
            Integer total=(list.size()+upsertConfig.getBatchLimit()-1) / upsertConfig.getBatchLimit();
            log.info("计划处理步骤 【{}】 步", total);
            List<List<T>> splitList= splitList(list, upsertConfig.getBatchLimit());
            int stepCount =1;
            for (List<T> ts : splitList) {
                log.info("处理步骤第 【{}】 步 ,总步数 【{}】", stepCount,total);
                execute(threadName, dataSource, ts);
                stepCount++;
            }
            log.info("分步操作完成✅");
        } else {
            execute(threadName, dataSource, list);
        }
        return true;
    }

    protected <T> Object execute(String threadName, DataSource dataSource, List<T> list) throws Exception {
        Future task = easyUpsertExecutor.submit((Callable) () -> {
            Thread.currentThread().setName(threadName);
            // 第一个参数 clazz
            Class clazz = list.get(0).getClass();
            Map<String, Map<String, String>> iEnumList = new HashMap<String, Map<String, String>>();
            if (null != userDictionaryService) {
                iEnumList = userDictionaryService.userDictionary(clazz);
            }
            iEnumList.putAll(CustomAnnotationConverter.collectionDictionary(clazz));
            String queryString = SQLConverter.upsertPreparedStatementSQL(list, clazz, iEnumList);
            if (upsertConfig.isPrintSql()) {
                log.info("执行sql: {}", queryString);
            }
            PreparedStatement pstm = null;
            Connection connection = null;
            boolean rs;
            try {
                connection = dataSource.getConnection();
//                System.out.println("connection = " + connection.toString());
                //获取PreparedStatement对象
                pstm = connection.prepareStatement(queryString);
                //执行SQL语句，获取结果集
                rs = pstm.execute();
            } catch (Exception e) {
                log.error(e.toString());
                throw new RuntimeException(e);
            } finally {
                if (pstm != null) {
                    try {
                        connection.close();
//                        System.out.println("关闭链接");
                        pstm.close();
                    } catch (SQLException throwables) {
//                        System.out.println("关闭链接异常");
                        throwables.printStackTrace();
                    }
                }
            }
            return rs;
        });
        return task.get();
    }

    private <T> List<List<T>> splitList(List<T> messagesList, int groupSize) {
        int length = messagesList.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize; // TODO
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(messagesList.subList(fromIndex, toIndex));
        }
        return newList;
    }


}