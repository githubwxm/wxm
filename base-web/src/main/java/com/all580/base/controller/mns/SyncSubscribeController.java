package com.all580.base.controller.mns;

import com.framework.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 数据同步MNS订阅
 * @date 2016/10/29 14:34
 */
@Controller
@RequestMapping("mns/subscribe")
@Slf4j
public class SyncSubscribeController extends BaseController {

    @RequestMapping(value = "sync", method = RequestMethod.POST)
    public void sync(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 请求合法性校验, json串的key必须是带双引号的
            String mnsMsgId = request.getHeader("x-mns-message-id");
            String tag = request.getHeader("x-mns-message-tag");
            log.info("MNS MESSAGE ID: {}, TAG: {}", mnsMsgId, tag);
            // 获取请求参数
            String msg = getReqParams(request);
            // 处理消息
            log.info("MNS 消息:{}, {}", mnsMsgId, msg);
            responseWrite(response, "OK");
        } catch (Exception e) {
            log.error("MNS SYNC ERROR", e);
            response.setStatus(500);
        }
    }
}
