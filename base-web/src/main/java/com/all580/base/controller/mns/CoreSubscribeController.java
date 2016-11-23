package com.all580.base.controller.mns;

import com.alibaba.fastjson.JSONObject;
import com.all580.base.aop.MnsSubscribeAspect;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.lang.exception.ApiException;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/11/23 14:51
 */
@Controller
@RequestMapping("mns/subscribe")
@Slf4j
public class CoreSubscribeController extends AbstractSubscribeController {
    @Autowired
    private ApplicationContext applicationContext;
    private static final String PROCESS = "process";
    private static final String ACTION = "action";
    private static final String CONTENT = "content";
    private static final String CREATE_TIME = "createTime";

    @RequestMapping(value = "core", method = RequestMethod.POST)
    public void core(HttpServletResponse response, ModelMap model) {
        String id = (String) model.get(MnsSubscribeAspect.MSG_ID);
        String msg = (String) model.get(MnsSubscribeAspect.MSG);
        if (StringUtils.hasText(msg)) {
            Map map = JSONObject.parseObject(msg, Map.class);
            validate(map);
            String action = map.get(ACTION).toString();
            String content = map.get(CONTENT).toString();
            Object time = map.get(CREATE_TIME);
            Date createTime = time == null ? null : DateFormatUtils.converToDateTime(time.toString());
            Object actionBean = applicationContext.getBean(action);
            if (actionBean == null) {
                throw new ApiException("MNS: "+action+" 订阅器不存在");
            }
            Method process = BeanUtils.findMethod(actionBean.getClass(), PROCESS, String.class, String.class, Date.class);
            if (process == null) {
                throw new ApiException("MNS: "+action+" 订阅器找不到方法: " + PROCESS);
            }
            try {
                Result result = (Result) process.invoke(actionBean, id, content, createTime);
                log.debug("调用订阅器回调Action:{}, Result:{}", action, result.toJsonString());
                if (!result.isSuccess()) {
                    throw new Exception(result.getError());
                }
                responseWrite(response, "OK");
            } catch (Exception e) {
                throw new ApiException("MNS: "+action+" 订阅器执行异常", e);
            }
        }
    }
}
