package com.wu.framework.easy.excel.util;

import com.wu.framework.easy.excel.stereotype.EasyExcel;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Jiawei Wu
 * @version 1.0
 * @describe : EasyExcel注解动态修改工具
 * @date : 2021/1/3 4:57 下午
 */
public class EasyExcelUtil {

    /**
     * @describe  在使用导出方法中使用发方法处理当前方法注解切换的作用
     * @return
     * @params 修改EasyExcel注解文件名
     * @author Jiawei Wu
     * @date 2021/1/3 5:25 下午
     **/
    public static void modifyCurrentMethodEasyExcelFileName(Class clazz, String fileName) {
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", fileName);
        try {
            modifyCurrentMethodEasyExcel(clazz, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     * @params 修改EasyExcel 注解
     * @author Jiawei Wu
     * @date 2021/1/3 5:24 下午
     **/
    public static void modifyCurrentMethodEasyExcel(Class clazz, Map<String, Object> params) throws NoSuchFieldException, IllegalAccessException {
        modifyCurrentMethodAnnotation(clazz, EasyExcel.class, params);
    }


    /**
     * @return
     * @params 修改方法注解
     * @author Jiawei Wu
     * @date 2021/1/3 4:58 下午
     **/
    public static void modifyCurrentMethodAnnotation(Class clazz, Class<? extends Annotation> methodAnnotation,
                                                     Map<String, Object> params) throws NoSuchFieldException, IllegalAccessException {
        StackTraceElement stackTraceElement = Arrays.stream(Thread.currentThread().getStackTrace()).filter(element -> element.getClassName().equals(clazz.getName())).findFirst().get();
        String methodName = stackTraceElement.getMethodName();
        Annotation annotation = Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.getName().equals(methodName)).
                filter(method -> !ObjectUtils.isEmpty(AnnotationUtils.getAnnotation(method, methodAnnotation)))
                .map(method -> AnnotationUtils.getAnnotation(method, methodAnnotation)).findFirst().get();
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
        memberValues.putAll(params);
    }
}
