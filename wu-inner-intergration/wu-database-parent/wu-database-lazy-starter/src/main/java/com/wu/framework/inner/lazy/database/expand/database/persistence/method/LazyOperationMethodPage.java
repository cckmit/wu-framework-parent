package com.wu.framework.inner.lazy.database.expand.database.persistence.method;

import com.wu.framework.inner.lazy.database.expand.database.persistence.domain.LazyTableAnnotation;
import com.wu.framework.inner.layer.stereotype.proxy.ProxyStrategicApproach;
import com.wu.framework.inner.lazy.database.domain.Page;
import com.wu.framework.inner.lazy.database.expand.database.persistence.constant.LayerOperationMethodCounts;
import com.wu.framework.inner.lazy.database.expand.database.persistence.domain.PersistenceRepository;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : Jia wei Wu
 * @version : 1.0
 * @describe: 根据ID更新  自定义数据库持久层操作方法我选择列表
 * @date : 2020/7/4 下午7:22
 */
@ProxyStrategicApproach(methodName = LayerOperationMethodCounts.PAGE)
public class LazyOperationMethodPage extends AbstractLazyOperationMethod {

    public static final String COUNT_SQL = "select count(1)  from (%s) as derived_table ";
    public static final String LIMIT_SQL = "select * from (%s) derived_table limit %s,%s ";
    public static final String DELIMITER = ";";

    private String countSql;
    private String limitSql;
    private Page page;

    @Override
    public PersistenceRepository getPersistenceRepository(Method method, Object[] args) throws Exception {
        String queryString = "";
        if (ObjectUtils.isEmpty(args)) {
            throw new IllegalArgumentException(String.format("fail invoke this method in method【%s】", method.getName()));
        }
        page = (Page) args[0];
        Class clazz = (Class) args[1];
        String sqlFormat = (String) args[2];
        Object[] params = (Object[]) args[3];

        if (null == sqlFormat) {
            LazyTableAnnotation lazyTableAnnotation = classLazyTableAnalyze(clazz);
            sqlFormat = String.format("select * from %s ", lazyTableAnnotation.getTableName());
        }
        String listSql = loadSqlParameters(sqlFormat, params);
        countSql = loadSqlParameters(COUNT_SQL, listSql);
        limitSql = loadSqlParameters(LIMIT_SQL, listSql, (page.getCurrent() - 1) * page.getSize(), page.getSize());

        queryString = countSql + DELIMITER + limitSql;
        PersistenceRepository persistenceRepository = new PersistenceRepository();
        persistenceRepository.setQueryString(queryString);
        persistenceRepository.setResultClass(clazz);
        return persistenceRepository;
    }

    /**
     * description 执行SQL 语句
     *
     * @return
     * @params
     * @author Jia wei Wu
     * @date 2020/11/22 上午11:02
     **/
    @Override
    public Object execute(PreparedStatement preparedStatement, PersistenceRepository persistenceRepository) throws SQLException {
        try {
            Connection connection = preparedStatement.getConnection();
            count(connection);
            ResultSet resultSet = preparedStatement.executeQuery(limitSql);
            List result = resultSetConverter(resultSet, persistenceRepository.getResultType());
            page.setRecord(result);
            return page;
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            preparedStatement.close();
        }
        return page;
    }

    /**
     * 获取分页数据总量
     *
     * @param connection
     * @return
     * @author Jiawei Wu
     * @date 2021/1/24 9:27 下午
     **/
    private void count(Connection connection) throws SQLException {
        PreparedStatement countPreparedStatement = connection.prepareStatement(countSql);
        ResultSet resultSet = countPreparedStatement.executeQuery();
        final List<Long> objects = resultSetConverter(resultSet, Long.class);
        long total = objects.get(0);
        long pages = (total + page.getSize() + 1) / page.getSize();
        page.setTotal(total);
        page.setPages(pages);
    }

}
