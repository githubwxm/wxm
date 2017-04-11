package com.all580.base.task.action;

import com.all580.base.manager.PushMsgManager;
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
 * @Description: 推送重试任务
 * @date 2017/1/19 17:46
 */
@Component(PushClientRetryAction.NAME)
@Slf4j
public class PushClientRetryAction implements JobRunner {
    public static final String NAME = "PUSH_CLIENT_RETRY_ACTION";
    @Autowired
    private PushMsgManager pushMsgManager;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        String url = params.get("url");
        String msg = params.get("msg");
        String sign = params.get("sign");
        pushMsgManager.postClient(url, msg, sign);
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("推送客户端信息重试任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "url", // url
                "msg", // msg
                "sign" // sign
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
