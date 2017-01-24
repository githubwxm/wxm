package com.all580.base.task.action;

import com.all580.base.manager.MnsEventCache;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.codec.TranscodeUtil;
import com.framework.common.mns.MnsSubscribeAction;
import com.framework.common.synchronize.LTSStatic;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

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
        if (actions == null || actions.size() == 0) {
            log.warn("MNS:{}, Action:{} 事件,没有订阅器.", params.get("msgId"), action);
            return new Result(Action.EXECUTE_SUCCESS, "没有订阅器");
        }
        Date createTime = DateFormatUtils.converToDateTime(params.get("time"));
        Object object = LTSStatic.SyncData.JSON_MAPPER.readValue(TranscodeUtil.base64StrToByteArray(params.get("content")), Object.class);
        for (MnsSubscribeAction subscribeAction : actions) {
            String name = CommonUtil.getProxyClassForInterface(subscribeAction, MnsSubscribeAction.class).getName();
            if (name.equals(params.get("class"))) {
                com.framework.common.Result result = subscribeAction.process(params.get("msgId"), object, createTime);
                log.debug("事件重试Action:{}, Class:{}, Result:{}", new Object[]{action, name, result.toJsonString()});
                if (!result.isSuccess()) {
                    throw new Exception(result.getError());
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
