package com.all580.payment.task.action;

import com.all580.order.api.service.PaymentCallbackService;
import com.all580.payment.api.conf.PaymentConstant;
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
 * @Description: 支付成功回调
 * @date 2016/10/10 20:41
 */
@Component(PaymentConstant.Actions.PAY_CALLBACK)
@Slf4j
public class PayCallbackAction implements JobRunner {
    @Autowired
    private PaymentCallbackService paymentCallbackService;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        Long ordCode = Long.parseLong(params.get("ordCode"));
        com.framework.common.Result result = paymentCallbackService.payCallback(ordCode, params.get("serialNum"), params.get("outTransId"));
        if (result == null || !result.isSuccess()) {
            return new Result(Action.EXECUTE_LATER, result == null ? null : result.toJsonString());
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("支付成功回调任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "ordCode",
                "serialNum"
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
