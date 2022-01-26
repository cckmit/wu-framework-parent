package com.wu.framework.easy.excel.web;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * description 未完成
 *
 * @author 吴佳伟
 * @date 2021/4/23 上午10:10
 */
@Deprecated
public class ExcelConfig implements InitializingBean {


    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;


    public ExcelConfig(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    /**
     * 添加拦截RequestExcelBody 注解并解析文件为对应数据
     */
    @Override
    public void afterPropertiesSet() throws Exception {


        RequestExcelBodyHandlerMethodArgumentResolver requestExcelBodyHandlerMethodArgumentResolver = new RequestExcelBodyHandlerMethodArgumentResolver(requestMappingHandlerAdapter.getMessageConverters());

        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        if (ObjectUtils.isEmpty(argumentResolvers)) {
            argumentResolvers = Arrays.asList(requestExcelBodyHandlerMethodArgumentResolver);
        } else {

            //参数解析器
            List<HandlerMethodArgumentResolver> tempArgumentResolvers = new ArrayList<>(Collections.singletonList(requestExcelBodyHandlerMethodArgumentResolver));
            tempArgumentResolvers.addAll(argumentResolvers);
            argumentResolvers = tempArgumentResolvers;

        }
        requestMappingHandlerAdapter.setArgumentResolvers(argumentResolvers);
    }


}
