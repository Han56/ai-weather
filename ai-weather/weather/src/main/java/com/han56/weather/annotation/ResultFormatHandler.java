package com.han56.weather.annotation;

import com.han56.weather.utils.ServiceResult;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class ResultFormatHandler {

    @Pointcut("@annotation(com.han56.weather.annotation.ResultFormat)")
    public void pointcut(){

    }


    @Around("pointcut()")
    private Object resultFormat(ProceedingJoinPoint joinPoint) throws Throwable{
        Object result;
        try {
            Object join = joinPoint.proceed();
            //切入
            if (join instanceof String || join instanceof Integer){
                ServiceResult<String> serviceResult = new ServiceResult<String>();
                serviceResult.setSuccess(true);
                serviceResult.setResult((String) join);
                serviceResult.setMessage((String) join);
                result = serviceResult;
            }else {
                //打日志
                result = join;
            }

        }catch (Throwable throwable){
            if (throwable instanceof Exception){
                result = handleException(throwable);
            }else {
                //打日志
                //抛异常
                throw throwable;
            }

        }
        return result;
    }

    //异常处理
    public Object handleException(Throwable throwable){
        ServiceResult result = new ServiceResult();
        result.setSuccess(false);
        result.setMessage(StringUtils.isBlank(throwable.getMessage())?throwable.getClass().getSimpleName():throwable.getMessage());
        return result;
    }

}
