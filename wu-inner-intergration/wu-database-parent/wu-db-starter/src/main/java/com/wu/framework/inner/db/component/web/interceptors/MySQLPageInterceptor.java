package com.wu.framework.inner.db.component.web.interceptors;

import com.github.pagehelper.Dialect;
import com.github.pagehelper.PageException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.cache.Cache;
import com.github.pagehelper.cache.CacheFactory;
import com.github.pagehelper.util.MSUtils;
import com.github.pagehelper.util.StringUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class MySQLPageInterceptor implements Interceptor {

    /**
     * 缓存count查询的ms
     */
    protected Cache<String, MappedStatement> msCountMap = null;

    private Dialect dialect = new PageHelper();
    private String default_dialect_class = "com.github.pagehelper.PageHelper";
    private Field additionalParametersField;
    private String countSuffix = "_COUNT";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            Executor executor = (Executor) invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            // 由于逻辑关系，只会进入一次
            if (args.length == 4) {
                // 4 个参数时
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                // 6 个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
            List resultList;
            // 调用方法判断是否需要进行分页，如果不需要，直接返回结果
            if (!dialect.skip(ms, parameter, rowBounds)) {
                // 反射获取动态参数
                String msId = ms.getId();
                Configuration configuration = ms.getConfiguration();
                Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField
                        .get(boundSql);
                // 判断是否需要进行 count 查询
                if (dialect.beforeCount(ms, parameter, rowBounds)) {
                    String countMsId = msId + countSuffix;
                    Long count;
                    // 先判断是否存在手写的 count 查询
                    MappedStatement countMs = getExistedMappedStatement(configuration, countMsId);
                    if (countMs != null) {
                        count = executeManualCount(executor, countMs, parameter, boundSql, resultHandler);
                    } else {
                        countMs = msCountMap.get(countMsId);
                        // 自动创建
                        if (countMs == null) {
                            // 根据当前的 ms 创建一个返回值为 Long 类型的 ms
                            countMs = MSUtils.newCountMappedStatement(ms, countMsId);
                            msCountMap.put(countMsId, countMs);
                        }
                        count = executeAutoCount(executor, countMs, parameter, boundSql, rowBounds, resultHandler);
                    }
                    // 处理查询总数
                    // 返回 true 时继续分页查询，false 时直接返回
                    if (!dialect.afterCount(count, parameter, rowBounds)) {
                        // 当查询总数为 0 时，直接返回空的结果
                        return dialect.afterPage(new ArrayList(), parameter, rowBounds);
                    }
                }
                // 判断是否需要进行分页查询
                if (dialect.beforePage(ms, parameter, rowBounds)) {
                    // 生成分页的缓存 key
                    CacheKey pageKey = cacheKey;
                    // 处理参数对象
                    parameter = dialect.processParameterObject(ms, parameter, boundSql, pageKey);
                    // 调用方言获取分页 sql
                    String pageSql = dialect.getPageSql(ms, boundSql, parameter, rowBounds, pageKey);

                    // 对sql进行改造
                    String changer = "";
                    int limitCount = 0;
                    if (pageSql.contains(PageSqlResolver.LIMIT_SIGN_EX)) {
                        changer = PageSqlResolver.LIMIT_SIGN_EX;
                        limitCount = 2;
                    } else if (pageSql.contains(PageSqlResolver.LIMIT_SIGN)) {
                        changer = PageSqlResolver.LIMIT_SIGN;
                        limitCount = 1;
                    }
                    pageSql = PageSqlResolver.resolveLimit(pageSql, changer);
                    List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
                    // 参数调度

                    // 参数偏移,需要调整limit 参数
                    if (pageSql.contains(PageSqlResolver.SQL_SIGN)) {
                        String sqlInCount = pageSql.substring(pageSql.indexOf(PageSqlResolver.SQL_SIGN));
                        // 统计limitable之后还有多少个?//
                        Integer count = 0;
                        for (int i = 0; i < sqlInCount.length(); i++) {
                            if (sqlInCount.indexOf("?", i) != -1) {
                                i = sqlInCount.indexOf("?", i);
                                count++;
                            }
                        }
                        // 获取临时的list
                        List<ParameterMapping> tempParameterMappingList = boundSql.getParameterMappings();
                        // count>0则 limit后面有参数,需要将limit的参数往前提
                        int size = tempParameterMappingList.size();
                        if (count > 0) {
                            // 只需要移动一个
                            if (limitCount == 1) {
                                // 获取最后一个参数
                                ParameterMapping p1 = tempParameterMappingList.remove(size - 1);
                                // 将参数插入到指定位置
                                tempParameterMappingList.add((size - 1 - count), p1);
                            }
                            // 需要移动两个
                            else if (limitCount == 2) {
                                // 获取最后两个参数,p1为倒数第一个 p2为倒数第二个
                                ParameterMapping p1 = tempParameterMappingList.remove(size - 1);
                                ParameterMapping p2 = tempParameterMappingList.remove(size - 2);
                                // 将参数插入到指定位置
                                tempParameterMappingList.add((size - 2 - count), p1);
                                tempParameterMappingList.add((size - 2 - count), p2);
                            }
                        }
                        parameterMappingList = tempParameterMappingList;
                    }

                    BoundSql pageBoundSql = new BoundSql(configuration, pageSql, parameterMappingList, parameter);
                    // 设置动态参数
                    for (String key : additionalParameters.keySet()) {
                        pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
                    }
                    // 执行分页查询
                    resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
                } else {
                    // 不执行分页的情况下，也不执行内存分页
                    resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
                }
            } else {
                // rowBounds用参数值，不使用分页插件处理时，仍然支持默认的内存分页
                resultList = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
            }
            return dialect.afterPage(resultList, parameter, rowBounds);
        } finally {
            dialect.afterAll();
        }
    }

    /**
     * 执行手动设置的 count 查询，该查询支持的参数必须和被分页的方法相同
     *
     * @param executor
     * @param countMs
     * @param parameter
     * @param boundSql
     * @param resultHandler
     * @return
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private Long executeManualCount(Executor executor, MappedStatement countMs, Object parameter, BoundSql boundSql,
                                    ResultHandler resultHandler) throws IllegalAccessException, SQLException {
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
        BoundSql countBoundSql = countMs.getBoundSql(parameter);
        Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey,
                countBoundSql);
        Long count = ((Number) ((List) countResultList).get(0)).longValue();
        return count;
    }

    /**
     * 执行自动生成的 count 查询
     *
     * @param executor
     * @param countMs
     * @param parameter
     * @param boundSql
     * @param rowBounds
     * @param resultHandler
     * @return
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private Long executeAutoCount(Executor executor, MappedStatement countMs, Object parameter, BoundSql boundSql,
                                  RowBounds rowBounds, ResultHandler resultHandler) throws IllegalAccessException, SQLException {
        Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
        // 创建 count 查询的缓存 key
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
        // 调用方言获取 count sql
        String countSql = dialect.getCountSql(countMs, boundSql, parameter, rowBounds, countKey);

        BoundSql countBoundSql = new BoundSql(countMs.getConfiguration(), countSql, boundSql.getParameterMappings(),
                parameter);
        // 当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
        for (String key : additionalParameters.keySet()) {
            countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        // 执行 count 查询
        Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey,
                countBoundSql);
        Long count = (Long) ((List) countResultList).get(0);
        return count;
    }

    /**
     * 尝试获取已经存在的在 MS，提供对手写count和page的支持
     *
     * @param configuration
     * @param msId
     * @return
     */
    private MappedStatement getExistedMappedStatement(Configuration configuration, String msId) {
        MappedStatement mappedStatement = null;
        try {
            mappedStatement = configuration.getMappedStatement(msId, false);
        } catch (Throwable t) {
            // ignore
        }
        return mappedStatement;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 缓存 count ms
        msCountMap = CacheFactory.createCache(properties.getProperty("msCountCache"), "ms", properties);
        String dialectClass = properties.getProperty("dialect");
        if (StringUtil.isEmpty(dialectClass)) {
            dialectClass = default_dialect_class;
        }
        try {
            Class<?> aClass = Class.forName(dialectClass);
            dialect = (Dialect) aClass.newInstance();
        } catch (Exception e) {
            throw new PageException(e);
        }
        dialect.setProperties(properties);

        String countSuffix = properties.getProperty("countSuffix");
        if (StringUtil.isNotEmpty(countSuffix)) {
            this.countSuffix = countSuffix;
        }

        try {
            // 反射获取 BoundSql 中的 additionalParameters 属性
            additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new PageException(e);
        }
    }

    static class PageSqlResolver {

        public static final String SQL_SIGN = "AS limitable";

        public static final String LIMIT_SIGN = "LIMIT ?";

        public static final String LIMIT_SIGN_EX = "LIMIT ?, ?";

        public static String resolveLimit(String pageSql, String changer) {
            if (pageSql == null) {
                return null;
            }

            // 如果需要特殊分页
            if (pageSql.contains(SQL_SIGN)) {

                pageSql = pageSql.replace(changer, "");
                StringBuilder sqlBuilder = new StringBuilder(pageSql);

                StringBuilder mae = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.indexOf(SQL_SIGN)));// mae
                // limitable
                StringBuilder uShiRo = new StringBuilder(
                        sqlBuilder.substring(sqlBuilder.indexOf(SQL_SIGN), sqlBuilder.length()));// 剩余的

                mae.insert(mae.lastIndexOf(")"), String.format(" %s", changer));

                return mae.append(uShiRo).toString();
            } else {
                return pageSql;
            }
        }

    }
}
