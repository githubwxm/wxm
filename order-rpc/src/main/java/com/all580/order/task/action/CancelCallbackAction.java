package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付成功分账失败取消任务
 * @date 2016/10/10 20:41
 */
@Component(OrderConstant.Actions.CANCEL_CALLBACK)
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class CancelCallbackAction implements JobRunner {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderId = Integer.parseInt(params.get("orderId"));
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("支付成功分账失败取消回调,订单不存在");
            throw new Exception("订单不存在");
        }

        com.framework.common.Result result = refundOrderManager.cancel(order);
        if (result.hasError()) {
            log.warn("支付成功分账失败取消回调,取消订单异常");
            throw new Exception(result.getError());
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("支付成功任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "orderId" // 订单ID
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        ParamsMapValidate.validate(params, rules);
    }
}
