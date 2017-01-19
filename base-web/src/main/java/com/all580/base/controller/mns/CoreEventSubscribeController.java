package com.all580.base.controller.mns;

import com.alibaba.fastjson.JSONObject;
import com.all580.base.aop.MnsSubscribeAspect;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.codec.TranscodeUtil;
import com.framework.common.mns.MnsSubscribeAction;
import com.framework.common.synchronize.LTSStatic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.lang.exception.ApiException;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 事件订阅器分发
 * @date 2016/11/23 14:51
 */
@Controller
@RequestMapping("mns/subscribe")
@Slf4j
public class CoreEventSubscribeController extends AbstractSubscribeController {
    private static final String ACTION = "action";
    private static final String CONTENT = "content";
    private static final String CREATE_TIME = "createTime";

    @Value("#{events}")
    private Map<String, List<MnsSubscribeAction>> events;

    @RequestMapping(value = "core/events", method = RequestMethod.POST)
    public void coreEvents(HttpServletResponse response, ModelMap model) throws Exception {
        String id = (String) model.get(MnsSubscribeAspect.MSG_ID);
        String msg = (String) model.get(MnsSubscribeAspect.MSG);
        if (StringUtils.hasText(msg)) {
            Map map = JSONObject.parseObject(msg, Map.class);
            validate(map);
            String action = map.get(ACTION).toString();
            String content = map.get(CONTENT).toString();
            Object time = map.get(CREATE_TIME);
            Date createTime = time == null ? null : DateFormatUtils.converToDateTime(time.toString());
            Object object = LTSStatic.SyncData.JSON_MAPPER.readValue(TranscodeUtil.base64StrToByteArray(content), Object.class);
            // 获取事件订阅器
            List<MnsSubscribeAction> actions = events.get(action);
            if (actions == null || actions.size() == 0) {
                log.warn("MNS:{}, Action:{} 事件,没有订阅器.", id, action);
                responseWrite(response, "OK");
                return;
            }
            for (MnsSubscribeAction subscribeAction : actions) {
                String name = subscribeAction.getClass().getName();
                try {
                    Result result = subscribeAction.process(id, object, createTime);
                    log.debug("调用订阅器回调Action:{}, Class:{}, Result:{}", new Object[]{action, name, result.toJsonString()});
                    if (!result.isSuccess()) {
                        throw new Exception(result.getError());
                    }
                    responseWrite(response, "OK");
                } catch (Exception e) {
                    // 后面做任务处理
                    log.error("MNS: "+action+", Class: " + name + " 订阅器执行异常", e);
                }
            }
        }
    }
}
