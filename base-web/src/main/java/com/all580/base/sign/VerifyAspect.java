package com.all580.base.sign;

import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by wxming on 2016/10/26 0026.
 */
@Component
@Aspect
@Slf4j
public class VerifyAspect {
    @Pointcut("execution(com.framework.common.Result com.all580.base.controller..*(..))")
    private void controller() {}

    @AfterReturning(value = "controller()", returning = "result")
    public void test(Result result) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        result.putExt("access_key", request.getAttribute("access_key"));

        log.warn("after.....{}-->{}", result, request.getAttribute("core_ep_id"));
    }
}
