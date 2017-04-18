package com.all580.base.controller.mns;

import com.alibaba.fastjson.JSONObject;
import com.all580.base.aop.MnsSubscribeAspect;
import com.all580.base.manager.MnsEventCache;
import com.all580.base.task.action.EventRetryAction;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.mns.MnsSubscribeAction;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;

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

    @Autowired
    private MnsEventCache mnsEventCache;

    @Value("${task.tracker}")
    private String taskTracker;

    @Value("${task.maxRetryTimes}")
    private Integer maxRetryTimes;

    @Autowired
    private JobClient jobClient;

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
            Date createTime = DateFormatUtils.converToDateTime(time.toString());
            // 获取事件订阅器
            Collection<MnsSubscribeAction> actions = mnsEventCache.getProcess(action);
            if (actions == null || actions.size() == 0) {
                log.warn("event subscribe {} {} {} {}", new Object[]{id, action, content, "没有订阅器"});
                return;
            }
            // TODO: 2017/3/24 这里先直接返回
            responseWrite(response, "OK");

            List<Job> jobs = new ArrayList<>();
            Object object = null;
            try {
                object = mnsEventCache.getObject(content, actions.iterator().next());
                if (object == null) {
                    throw new Exception("事件内容为空");
                }
                for (MnsSubscribeAction subscribeAction : actions) {
                    String name = CommonUtil.getProxyClassForInterface(subscribeAction, MnsSubscribeAction.class).getName();
                    try {
                        Result result = subscribeAction.process(id, object, createTime);
                        log.error("event process-result {} {} {} {}", new Object[]{id, action, content, String.format("订阅器:%s执行完成:%s", name, result.toJsonString())});
                        if (!result.isSuccess()) {
                            throw new Exception(result.getError());
                        }
                    } catch (Exception e) {
                        log.error("event process-fail {} {} {} {}", new Object[]{id, action, content, "订阅器执行异常,添加任务重试..."});
                        jobs.add(createRetryJob(id, content, time, action, name));
                    }
                }
            } catch (Exception e) {
                if (object == null) {
                    log.error("event subscribe {} {} {} {}", new Object[]{id, action, content, "解析事件内容异常,添加任务重试..."});
                    addRetryTask(jobs, actions, id, content, time, action);
                } else {
                    throw e;
                }
            }

            if (!jobs.isEmpty() && jobs.size() > 0) {
                jobClient.submitJob(jobs);
            }
        }
    }

    private void addRetryTask(List<Job> jobs, Collection<MnsSubscribeAction> actions, String id, String content, Object time, String action) {
        for (MnsSubscribeAction subscribeAction : actions) {
            String name = CommonUtil.getProxyClassForInterface(subscribeAction, MnsSubscribeAction.class).getName();
            jobs.add(createRetryJob(id, content, time, action, name));
        }
    }

    private Job createRetryJob(String id, String content, Object time, String action, String name) {
        Job job = new Job();
        job.setTaskId("EVENT-RETRY-JOB-" + UUIDGenerator.getUUID());
        job.setParam("msgId", id);
        job.setParam("content", content);
        job.setParam("time", time.toString());
        job.setParam("class", name);
        job.setParam("action", action);
        job.setParam("$ACTION$", EventRetryAction.NAME);
        job.setTaskTrackerNodeGroup(taskTracker);
        if (maxRetryTimes != null) {
            job.setMaxRetryTimes(maxRetryTimes);
        }
        job.setNeedFeedback(false);
        log.info("事件错误新增TASK: {}-{}-{}", new Object[]{job.getTaskId(), id, name});
        return job;
    }
}
