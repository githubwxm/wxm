package com.all580.base.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/11/23 14:03
 */
@Component
@Aspect
@Slf4j
public class MnsSubscribeAspect {
    public static final String MSG_ID = "MSG_ID";
    public static final String TAG = "TAG";
    public static final String MSG = "MSG";

    @Pointcut("this(com.all580.base.controller.mns.AbstractSubscribeController) && execution(void com.all580.base.controller.mns..*(..)) && args(response, model)")
    private void mapping(HttpServletResponse response, ModelMap model) {}

    @Around(value = "mapping(response, model) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", argNames = "thisJoinPoint,response,model")
    public Object doNothing(ProceedingJoinPoint thisJoinPoint, HttpServletResponse response, ModelMap model) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Method getReqParams = BeanUtils.findMethod(thisJoinPoint.getThis().getClass(), "getReqParams", HttpServletRequest.class);
        if (getReqParams == null) {
            throw new ApiException("Mns 实现类找不到方法: getReqParams");
        }
        // 请求合法性校验, json串的key必须是带双引号的
        String mnsMsgId = request.getHeader("x-mns-message-id");
        String tag = request.getHeader("x-mns-message-tag");
        log.info("MNS MESSAGE ID: {}, TAG: {}", mnsMsgId, tag);
        // 获取请求参数
        String msg = (String) getReqParams.invoke(thisJoinPoint.getThis(), request);
        // 处理消息
        log.info("MNS: {} 消息: {}", mnsMsgId, msg);
        // 保存消息

        // 处理消息
        model.put(MSG_ID, mnsMsgId);
        model.put(TAG, tag);
        model.put(MSG, msg);
        thisJoinPoint.proceed(new Object[]{response, model});

        // 处理成功
        log.debug("MNS:{} TAG:{} 处理成功.", mnsMsgId, tag);
        return null;
    }
}
