package com.all580.base.manager;

import com.all580.base.adapter.push.PushMsgAdapter;
import com.all580.base.task.action.PushClientRetryAction;
import com.all580.base.task.action.PushRetryAction;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpPushService;
import com.framework.common.Result;
import com.framework.common.lang.UUIDGenerator;
import com.github.ltsopensource.core.domain.Job;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.lang.exception.ApiException;
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

    public Map getMsg(PushMsgAdapter adapter, Map map, Map config, String originMsg) {
        return adapter.parseMsg(map, config, originMsg);
    }

    public List getUrls(String epId, String id) {
        Result<?> result = epPushService.selectByEpId(epId);
        if (result == null || !result.isSuccess()) {
            throw new ApiException("查询企业推送配置异常: " + id + ":" + (result == null ? "null" : result.getError()));
        }
        return (List) result.get();
    }

    public void postClient(String url, String content, String sign) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        StringEntity postingString = new StringEntity(content);// json传递
        request.setEntity(postingString);
        request.setHeader("Content-type", "application/json");
        request.setHeader("sign", sign);
        log.debug("请求运营平台:URL:{},CONTENT:{},SIGN:{}", new Object[]{url, content, sign});
        HttpResponse response = httpClient.execute(request);
        String responseContent = IOUtils.toString(response.getEntity().getContent());
        JSONObject res = JSONObject.fromObject(responseContent);
        if (res == null || res.isNullObject() || res.getInt("code") != 200) {
            log.warn("请求运营平台错误:{}-{}", url, responseContent);
            throw new ApiException("请求运营平台错误");
        }
        log.debug("请求运营平台:URL:{},RES:{}", url, responseContent);
    }

    public void pushForAddJob(String id, String epId, String url, String type, String msg, Map map, Map m, List<Job> jobs) {
        try {
            PushMsgAdapter adapter = getAdapter(type);
            Map content = getMsg(adapter, map, m, msg);
            String sign = adapter.sign(epId, url, content, map, m);
            if (sign != null) {
                content.put("sign", sign);
            }
            adapter.push(epId, url, content, map, m);
            log.info("推送信息结束:{} URL:{} CONTENT:{} 成功", new Object[]{id, url, content});
        } catch (Exception e) {
            log.warn("推送信息结束:{} URL:{} 推送失败", new Object[]{id, url, e});
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
        job.setMaxRetryTimes(maxRetryTimes);
        job.setNeedFeedback(false);
        log.info("推送信息错误新增TASK: {}-{}-{}", new Object[]{job.getTaskId(), id, url});
        return job;
    }

    public Job addClientJob(String url, String msg, String sign) {
        Job job = new Job();
        job.setTaskId("PUSH-RETRY-JOB-" + UUIDGenerator.getUUID());
        job.setParam("url", url);
        job.setParam("msg", msg);
        job.setParam("sign", sign);
        job.setParam("$ACTION$", PushClientRetryAction.NAME);
        job.setTaskTrackerNodeGroup(taskTracker);
        job.setMaxRetryTimes(maxRetryTimes);
        job.setNeedFeedback(false);
        log.info("推送客户端信息错误新增TASK: {}-{}-{}", new Object[]{job.getTaskId(), msg, url});
        return job;
    }
}
