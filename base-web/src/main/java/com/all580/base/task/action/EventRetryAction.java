package com.all580.base.task.action;

import com.all580.base.manager.MnsEventCache;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.mns.MnsSubscribeAction;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 事件重试任务
 * @date 2017/1/19 17:46
 */
@Component(EventRetryAction.NAME)
@Slf4j
public class EventRetryAction implements JobRunner {
    public static final String NAME = "EVENT_RETRY_ACTION";
    @Autowired
    private MnsEventCache mnsEventCache;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        String action = params.get("action");
        Collection<MnsSubscribeAction> actions = mnsEventCache.getProcess(action);
        String mnsId = params.get("msgId");
        if (actions == null || actions.size() == 0) {
            log.warn("MNS:{}, Action:{} 事件,没有订阅器.", mnsId, action);
            return new Result(Action.EXECUTE_SUCCESS, "没有订阅器");
        }
        Date createTime = DateFormatUtils.converToDateTime(params.get("time"));
        String content = params.get("content");
        Object object = mnsEventCache.getObject(content, actions.iterator().next());
        for (MnsSubscribeAction subscribeAction : actions) {
            String name = CommonUtil.getProxyClassForInterface(subscribeAction, MnsSubscribeAction.class).getName();
            if (name.equals(params.get("class"))) {
                String msgId = String.format("%s$RETRY%s%d", mnsId, jobContext.getJob().getTaskId(), jobContext.getJobExtInfo().getRetryTimes() + 1);
                try {
                    com.framework.common.Result result = subscribeAction.process(msgId, object, createTime);
                    if (!result.isSuccess()) {
                        throw new Exception(result.getError());
                    }
                    log.debug("MNS:{},事件重试Action:{} Content:{}, Task:{}, Class:{}, Result:{}",
                            new Object[]{mnsId, action, JsonUtils.toJson(object), jobContext.getJob().getTaskId(), name, true});
                } catch (Exception e) {
                    log.warn("MNS:{},事件重试Action:{} Content:{}, Task:{}, Class:{}, Result:{}",
                            new Object[]{mnsId, action, JsonUtils.toJson(object), jobContext.getJob().getTaskId(), name, false, e});
                    throw e;
                }
                break;
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
            throw new RuntimeException("事件重试任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "msgId", // MNS ID
                "content", // base64内容
                "action",
                "time",
                "class"
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
