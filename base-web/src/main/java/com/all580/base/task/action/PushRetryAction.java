package com.all580.base.task.action;

import com.alibaba.fastjson.JSONObject;
import com.all580.base.adapter.push.PushMsgAdapter;
import com.all580.base.manager.PushMsgManager;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 推送重试任务
 * @date 2017/1/19 17:46
 */
@Component(PushRetryAction.NAME)
@Slf4j
public class PushRetryAction implements JobRunner {
    public static final String NAME = "PUSH_RETRY_ACTION";
    @Autowired
    private PushMsgManager pushMsgManager;

    @Autowired
    private JobClient jobClient;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        String msgId = params.get("msgId");
        String url = params.get("url");
        String type = params.get("type");
        String msg = params.get("content");
        String epId = params.get("epId");
        Map map = JSONObject.parseObject(msg, Map.class);
        // URL 为空则全部推送
        if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(type)) {
            log.debug("推送信息:{} URL:{}", msgId, url);
            PushMsgAdapter adapter = pushMsgManager.getAdapter(type);
            String content = pushMsgManager.getMsg(adapter, map, msg);
            adapter.push(epId, url, content, null);
            log.info("推送信息:{} URL:{} 成功", msgId, url);
        } else {
            List list = pushMsgManager.getUrls(epId, msgId);
            if (list == null) {
                log.warn("推送信息:{} 没有查询到配置", msgId);
                return new Result(Action.EXECUTE_SUCCESS, "没有推送地址");
            }
            List<Job> jobs = new ArrayList<>();
            for (Object o : list) {
                Map m = (Map) o;
                url = CommonUtil.objectParseString(m.get("url"));
                if (StringUtils.isEmpty(url)) {
                    log.warn("推送信息:{} URL为空", msgId);
                    continue;
                }
                type = CommonUtil.objectParseString(m.get("type"));
                // push
                log.debug("推送信息:{} URL:{}", msgId, url);
                pushMsgManager.pushForAddJob(msgId, epId, url, type, msg, map, m, jobs);
            }
            if (!jobs.isEmpty() && jobs.size() > 0) {
                jobClient.submitJob(jobs);
            }
        }

        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("推送信息重试任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "msgId", // MNS ID
                "epId", // epId
                "content" // base64内容
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
