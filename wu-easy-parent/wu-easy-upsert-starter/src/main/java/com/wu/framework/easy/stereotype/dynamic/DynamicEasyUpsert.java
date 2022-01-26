package com.wu.framework.easy.stereotype.dynamic;


import com.wu.framework.easy.stereotype.dynamic.toolkit.DynamicEasyUpsertDSContextHolder;
import com.wu.framework.easy.stereotype.upsert.IEasyUpsert;
import com.wu.framework.easy.stereotype.upsert.config.UpsertConfig;
import com.wu.framework.easy.stereotype.upsert.dynamic.EasyUpsertDS;
import com.wu.framework.easy.stereotype.upsert.dynamic.EasyUpsertStrategy;
import com.wu.framework.easy.stereotype.upsert.enums.EasyUpsertType;
import com.wu.framework.easy.stereotype.upsert.exceptions.IllegalDataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description EasyUpsert获取IEasyUpsert和注解CustomDS实现类
 *
 * @author Jia wei Wu
 * @date 2020/9/14 下午2:55
 */
public class DynamicEasyUpsert extends AbstractDynamicEasyUpsert implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DynamicEasyUpsert.class);

    private EasyUpsertType primary;
    private EasyUpsertDS primaryEasyUpsertDS;
    private Map<EasyUpsertType, IEasyUpsert> iEasyUpsertMap = new LinkedHashMap<>();


    private List<IEasyUpsert> iEasyUpsertList;
    private UpsertConfig upsertConfig;

    public DynamicEasyUpsert(List<IEasyUpsert> iEasyUpsertList, UpsertConfig upsertConfig) {
        this.iEasyUpsertList = iEasyUpsertList;
        this.upsertConfig = upsertConfig;
    }

    /**
     * 获取当前线程注解
     *
     * @return
     */
    @Override
    public EasyUpsertDS determineEasyUpsertDS() {
        return DynamicEasyUpsertDSContextHolder.peek();
    }

    /**
     * 获取当前线程所需要的接口类型
     *
     * @return
     */
    @Override
    public IEasyUpsert determineIEasyUpsert() {
        return getIEasyUpsert(DynamicEasyUpsertDSContextHolder.peek());
    }


    private IEasyUpsert determinePrimaryDataSource() {
        log.info("使用默认方式:【{}】", primary);
        return iEasyUpsertMap.get(primary);
    }

    private IEasyUpsert getIEasyUpsert(EasyUpsertDS peek) {
        if (ObjectUtils.isEmpty(peek) || peek.type().equals(EasyUpsertType.AUTO)) {
            return determinePrimaryDataSource();
        } else if (iEasyUpsertMap.containsKey(peek.type())) {
            log.info("使用方式:【{}】", peek.type());
            return iEasyUpsertMap.get(peek.type());
        } else {
            throw new IllegalDataSourceException("不能找到类型为" + peek + "的数据源");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //        数据源类型存放
        primaryEasyUpsertDS = defaultCustomDS();
        if (!ObjectUtils.isEmpty(iEasyUpsertList)) {
            log.info("EasyUpsert 初始共加载 {} 种方式", iEasyUpsertList.size());
            for (IEasyUpsert iEasyUpsert : iEasyUpsertList) {
                EasyUpsertStrategy easyUpsertStrategy = AnnotationUtils.findAnnotation(iEasyUpsert.getClass(), EasyUpsertStrategy.class);
                if (null != easyUpsertStrategy) {
                    log.info("EasyUpsert 动态方式-加载 {} 成功", easyUpsertStrategy.easyUpsertType());
                    iEasyUpsertMap.put(easyUpsertStrategy.easyUpsertType(), iEasyUpsert);
                }
            }
            if (upsertConfig.getEasyUpsertType().equals(EasyUpsertType.AUTO)) {
                if (ObjectUtils.isEmpty(iEasyUpsertMap)) {
                    return;
                }
                primary = iEasyUpsertMap.keySet().iterator().next();
                log.info("当前的默认方式是 {} ", primary);
            } else {
                if (iEasyUpsertMap.containsKey(upsertConfig.getEasyUpsertType())) {
                    primary = upsertConfig.getEasyUpsertType();
                    log.info("当前的默认方式是 {} ", upsertConfig.getEasyUpsertType());
                } else {
                    primary = iEasyUpsertMap.keySet().iterator().next();
                    log.info("无法找到方式 【{}】 使用默认方式【{}】 ", upsertConfig.getEasyUpsertType(), primary);
                }
            }
        }

    }

    /**
     * 默认 注解
     *
     * @return
     */
    private EasyUpsertDS defaultCustomDS() {
        return new EasyUpsertDS() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return EasyUpsertDS.class;
            }

            /**
             * 数据源名称(MYSQL多数据源有效)
             *
             * @return
             */
            @Override
            public String value() {
                return "";
            }

            @Override
            public String name() {
                return "";
            }

            /**
             * 数据源类型 默认MySQL
             */
            @Override
            public EasyUpsertType type() {
                return upsertConfig.getEasyUpsertType();
            }
        };
    }

}
