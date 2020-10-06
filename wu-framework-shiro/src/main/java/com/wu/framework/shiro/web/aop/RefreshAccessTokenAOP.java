package com.wu.framework.shiro.web.aop;

import com.wu.framework.shiro.annotation.RefreshAccessToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


@Aspect
public class RefreshAccessTokenAOP {

    @Pointcut("@annotation(refreshAccessToken)")
    public void refreshAccessTokenPointcut(RefreshAccessToken refreshAccessToken) {
        System.out.println("切面刷新用户");
    }

    @Around("refreshAccessTokenPointcut(refreshAccessToken)")
    private Object around(ProceedingJoinPoint point, RefreshAccessToken refreshAccessToken) throws Throwable {
        // 异常日志信息
        String exceptionLog = null;
        StackTraceElement[] stackTrace = null;
        // 是否执行异常
        boolean isException = false;
        //方法返回结果
        Object result = null;
        // 方法开始时间戳
        final Long start = System.currentTimeMillis();
        try {
            //目标方法开始执行
            result = point.proceed();
        } catch (Throwable throwable) {
            isException = true;
            exceptionLog = throwable.getMessage();
            stackTrace = throwable.getStackTrace();
            throw throwable;
        } finally {
            // 异步执行记录数据

        }
        //返回目标方法结果
        return result;
    }
}