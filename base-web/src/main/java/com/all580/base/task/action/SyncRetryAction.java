package com.all580.base.task.action;

import com.aliyun.mns.model.TopicMessage;
import com.framework.common.lang.codec.TranscodeUtil;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 同步数据重试任务
 * @date 2017/1/19 17:46
 */
@Component("SYNC_NEW_ACTION")
@Slf4j
public class SyncRetryAction implements JobRunner {
    @Autowired
    private TopicPushManager topicPushManager;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        String topic = params.get("topic");
        String tag = params.get("tag");
        String value = params.get("value");
        value = TranscodeUtil.base64StrToStr(value);
        TopicMessage message = topicPushManager.push(topic, tag, value, false);
        return new Result(Action.EXECUTE_SUCCESS, message.getMessageId());
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("同步重试任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "topic",
                "tag",
                "value"
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
