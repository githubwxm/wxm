package com.all580.voucherplatform.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.all580.voucherplatform.api.service.All580Service;
import com.all580.voucherplatform.api.service.OrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.mns.OssStoreManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
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
    @Autowired
    private OssStoreManager ossStoreManager;
    @Autowired
    private OrderService orderService;

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

    @RequestMapping(value = "oss/event/put/consume")
    @ResponseBody
    public String consumeSyncOssPut(HttpServletRequest request) {
        String messageId = request.getHeader("x-mns-message-id");
        log.info("接收到MNS消息 MSGID={}", messageId);
        try {
            String content = getReqParams(request);
            JSONObject jsonObject = JSONObject.parseObject(content);
            JSONArray events = jsonObject.getJSONArray("events");
            if (events != null && !events.isEmpty()) {
                for (int i = 0; i < events.size(); i++) {
                    JSONObject item = events.getJSONObject(i);
                    String key = item.getJSONObject("oss").getJSONObject("object").getString("key");
                    log.info("OSS PUT 事件通知 key: {}", key);
                    syncConsume(key);
                }
            }
        } catch (IOException ex) {
            log.error("MNS读取请求数据流异常", ex);
        }
        return "success";
    }

    private byte[] getRequestBuffer(HttpServletRequest request) throws IOException {
        byte[] requestBuffer = new byte[request.getContentLength()];
        InputStream inputStream = request.getInputStream();
        inputStream.read(requestBuffer, 0, requestBuffer.length);
        return requestBuffer;
    }

    private void syncConsume(String key) {
        try {
            String folder = System.getProperty("java.io.tmpdir");
            String name = key.substring(key.lastIndexOf("/") + 1);
            File file = new File(folder, name);
            ossStoreManager.download(key, file.getAbsolutePath());
            InputStream inputStream = new FileInputStream(file);
            int line = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf8")));
            try {
                String lineStr = null;
                while ((lineStr = reader.readLine()) != null) {
                    try {
                        Map params = null;
                        try {
                            params = JsonUtils.json2Map(lineStr);
                        } catch (Exception ignored) {}
                        if (params == null) {
                            log.warn("解析文件 {} 第 {} 行 数据结构异常", key, line);
                            continue;
                        }
                        orderService.consumeSync(params, key.endsWith(".group"));
                        log.info("同步文件 {} 核销数据: {}", key, lineStr);
                    } catch (Throwable e) {
                        if (e instanceof ApiException || e instanceof ParamsMapValidationException) {
                            log.warn("解析文件 {} 第 {} 行:{} 失败:{}", new Object[]{key, line, lineStr, e.getMessage()});
                        } else {
                            log.warn("解析文件 {} 第 {} 行:{} 异常", new Object[]{key, line, lineStr, e});
                        }
                    }
                    line++;
                }
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String id = name.substring(0, name.indexOf("."));
            orderService.consumeSyncComplete(CommonUtil.objectParseInteger(id, -1));
            ossStoreManager.getClient().deleteObject(ossStoreManager.getBucket(), key);
            log.info("文件 {} 同步完成一共 {} 行", key, line);
        } catch (Throwable e) {
            log.warn("下载OSS文件 {} 异常", key, e);
        }
    }
}
