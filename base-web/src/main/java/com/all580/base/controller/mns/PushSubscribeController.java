package com.all580.base.controller.mns;

import com.alibaba.fastjson.JSONObject;
import com.all580.base.aop.MnsSubscribeAspect;
import com.all580.base.task.action.PushRetryAction;
import com.all580.ep.api.service.EpPushService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.lang.exception.ApiException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 推送MNS订阅
 * @date 2016/10/19 15:44
 */
@Controller
@RequestMapping("mns/subscribe")
@Slf4j
public class PushSubscribeController extends AbstractSubscribeController {
    @Autowired
    private EpPushService epPushService;
    @Value("${task.tracker}")
    private String taskTracker;

    @Value("${task.maxRetryTimes}")
    private Integer maxRetryTimes;

    @Autowired
    private JobClient jobClient;

    @RequestMapping(value = "push", method = RequestMethod.POST)
    public void push(HttpServletResponse response, ModelMap model) {
        String id = (String) model.get(MnsSubscribeAspect.MSG_ID);
        String msg = (String) model.get(MnsSubscribeAspect.MSG);
        if (!StringUtils.hasText(msg)) {
            log.warn("推送信息:{} 推送信息为空", id);
            responseWrite(response, "OK");
            return;
        }
        Map map = JSONObject.parseObject(msg, Map.class);
        String opCode = CommonUtil.objectParseString(map.get("op_code"));
        String refundResult = CommonUtil.objectParseString(map.get("refund_result"));
        if (opCode != null && "REFUND".equals(opCode) && refundResult != null && "REJECT".equals(refundResult)) {
            map.put("op_code", "REFUND_FAIL");
            msg = JsonUtils.toJson(map);
        }
        String epId = CommonUtil.objectParseString(map.get("ep_id"));
        try {
            responseWrite(response, "OK");
            Result<?> result = epPushService.selectByEpId(epId);
            if (result == null || !result.isSuccess()) {
                throw new ApiException("查询企业推送配置异常: " + id + ":" + (result == null ? "null" : result.getError()));
            }
            List list = (List) result.get();
            if (list == null) {
                log.warn("推送信息:{} 没有查询到配置", id);
                return;
            }
            List<Job> jobs = new ArrayList<>();
            for (Object o : list) {
                Map m = (Map) o;
                String url = CommonUtil.objectParseString(m.get("url"));
                if (StringUtils.isEmpty(url)) {
                    log.warn("推送信息:{} URL为空", id);
                    continue;
                }
                // push
                log.debug("推送信息:{} URL:{}", id, url);
                try {
                    String res = HttpUtils.postJson(url, msg, "UTF-8");
                    if (!res.equalsIgnoreCase("ok")) {
                        throw new ApiException(res);
                    }
                } catch (Exception e) {
                    log.warn("推送信息:{} URL:{} 推送失败", new Object[]{id, url, e});
                    jobs.add(addJob(id, msg, url, epId));
                }
                log.info("推送信息:{} URL:{} 成功", id, url);
            }
            if (!jobs.isEmpty() && jobs.size() > 0) {
                jobClient.submitJob(jobs);
            }
        } catch (Exception e) {
            log.error("MNS PUSH ERROR", e);
            jobClient.submitJob(addJob(id, msg, null, epId));
        }
    }

    private Job addJob(String id, String content, String url, String epId) {
        Job job = new Job();
        job.setTaskId("PUSH-RETRY-JOB-" + UUIDGenerator.getUUID());
        job.setParam("msgId", id);
        job.setParam("content", content);
        if (!StringUtils.isEmpty(url)) {
            job.setParam("url", url);
        }
        job.setParam("epId", epId);
        job.setParam("$ACTION$", PushRetryAction.NAME);
        job.setTaskTrackerNodeGroup(taskTracker);
        if (maxRetryTimes != null) {
            job.setMaxRetryTimes(maxRetryTimes);
        }
        job.setNeedFeedback(false);
        log.info("推送信息错误新增TASK: {}-{}-{}", new Object[]{job.getTaskId(), id, url});
        return job;
    }
}
