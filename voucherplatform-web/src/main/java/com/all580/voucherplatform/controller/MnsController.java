package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.All580Service;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-20.
 */
@Controller
@RequestMapping("api/mns")
@Slf4j
public class MnsController extends BaseController {

    @Autowired
    private All580Service all580Service;

    @RequestMapping(value = "ticket", method = RequestMethod.POST)
    @ResponseBody
    public String ticket(HttpServletRequest request) {
        String messageId = request.getHeader("x-mns-message-id");
        String tagName = request.getHeader("x-mns-message-tag");
        log.info("接收到MNS消息 MSGID={},TAG={}", new Object[]{messageId, tagName});
        String content = null;
        try {
            byte[] buffer = getRequestBuffer(request);
            content = new String(buffer, "utf-8");
        } catch (IOException ex) {
            log.error("MNS读取请求数据流异常", ex);
        }
        Map map = JsonUtils.json2Map(content);
        log.info("接收到MNS消息 content={}", content);
        Result result = all580Service.supplyProcess(map);
        if (!result.isSuccess()) {
            log.debug(result.getError());
        }
        return "success";
    }

    private byte[] getRequestBuffer(HttpServletRequest request) throws IOException {
        byte[] requestBuffer = new byte[request.getContentLength()];
        InputStream inputStream = request.getInputStream();
        inputStream.read(requestBuffer, 0, requestBuffer.length);
        return requestBuffer;
    }
}
