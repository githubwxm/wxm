package com.all580.base.manager;

import com.all580.base.adapter.push.PushMsgAdapter;
import com.all580.base.task.action.PushRetryAction;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpPushService;
import com.framework.common.Result;
import com.framework.common.lang.UUIDGenerator;
import com.github.ltsopensource.core.domain.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/4/6 9:37
 */
@Component
@Slf4j
public class PushMsgManager {
    @Autowired
    private EpPushService epPushService;
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${task.tracker}")
    private String taskTracker;

    @Value("${task.maxRetryTimes}")
    private Integer maxRetryTimes;

    private Map<PushMsgAdapter, String> msgAdapterStringMap = new HashMap<>();

    public PushMsgAdapter getAdapter(String type) {
        try {
            PushMsgAdapter bean = applicationContext.getBean(EpConstant.PUSH_ADAPTER + (StringUtils.isEmpty(type) ? "GENERAL" : type), PushMsgAdapter.class);
            if (bean == null) {
                throw new Exception("null");
            }
            return bean;
        } catch (Exception e) {
            log.warn("获取适配器异常", e);
            throw new ApiException("没找到推送适配器:" + type);
        }
    }

    public String getMsg(PushMsgAdapter adapter, Map map, String msg) {
        if (msgAdapterStringMap.containsKey(adapter)) {
            return msgAdapterStringMap.get(adapter);
        }
        msg = adapter.parseMsg(map, msg);
        msgAdapterStringMap.put(adapter, msg);
        return msg;
    }

    public List getUrls(String epId, String id) {
        Result<?> result = epPushService.selectByEpId(epId);
        if (result == null || !result.isSuccess()) {
            throw new ApiException("查询企业推送配置异常: " + id + ":" + (result == null ? "null" : result.getError()));
        }
        return (List) result.get();
    }

    public void pushForAddJob(String id, String epId, String url, String type, String msg, Map map, Map m, List<Job> jobs) {
        try {
            PushMsgAdapter adapter = getAdapter(type);
            String content = getMsg(adapter, map, msg);
            adapter.push(epId, url, content, m);
            log.info("推送信息:{} URL:{} 成功", id, url);
        } catch (Exception e) {
            log.warn("推送信息:{} URL:{} 推送失败", new Object[]{id, url, e});
            jobs.add(addJob(id, msg, url, type, epId));
        }
    }

    public Job addJob(String id, String content, String url, String type, String epId) {
        Job job = new Job();
        job.setTaskId("PUSH-RETRY-JOB-" + UUIDGenerator.getUUID());
        job.setParam("msgId", id);
        job.setParam("content", content);
        if (!StringUtils.isEmpty(url)) {
            job.setParam("url", url);
        }
        if (!StringUtils.isEmpty(type)) {
            job.setParam("type", type);
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
