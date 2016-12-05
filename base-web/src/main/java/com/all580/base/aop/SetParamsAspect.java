package com.all580.base.aop;

import com.all580.ep.api.conf.EpConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.lang.exception.ApiException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/11/23 14:03
 */
@Component
@Aspect
@Slf4j
public class SetParamsAspect {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) && execution(* com.all580.base.controller..*(..)) && args(params)")
    private void mapping(Map params) {}

    @Before(value = "mapping(params)", argNames = "params")
    public void setParams(Map params) {
        log.debug("params: {}", params);
        Object coreEpId = RequestContextHolder.getRequestAttributes().getAttribute(EpConstant.EpKey.CORE_EP_ID, ServletRequestAttributes.SCOPE_REQUEST);
        if (coreEpId != null && !params.containsKey(EpConstant.EpKey.CORE_EP_ID)) {
            params.put(EpConstant.EpKey.CORE_EP_ID, coreEpId);
        }
    }
}
