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
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description: 事件触发失败重新触发任务
 * @date 17-5-4 上午11:07
 */
@Component(EventFireAction.NAME)
@Slf4j
public class EventFireAction implements JobRunner {
    public static final String NAME = "EVENT_FIRE_ACTION";
    @Autowired
    private TopicPushManager topicPushManager;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        String topic = params.get("topic");
        String tag = params.get("tag");
        String action = params.get("action");
        String content = params.get("content");
        content = TranscodeUtil.base64StrToStr(content);
        TopicMessage message = topicPushManager.fireEvent(topic, tag, action, content);
        return new Result(Action.EXECUTE_SUCCESS, message.getMessageId());
    }

    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("事件重新触发任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "topic", // MNS ID
                "tag", // base64内容
                "action",
                "content"
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
