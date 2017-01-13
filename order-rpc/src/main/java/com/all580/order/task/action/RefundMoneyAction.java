package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
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
 * @Description: 退款任务
 * @date 2016/10/20 20:51
 */
@Component(OrderConstant.Actions.REFUND_MONEY)
@Slf4j
public class RefundMoneyAction implements JobRunner {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);
        // 退款
        Order order = orderMapper.selectBySN(Long.valueOf(params.get("ordCode")));
        if (order == null) {
            log.warn("退款失败任务,订单不存在");
            throw new Exception("订单不存在");
        }
        Long serialNum = Long.valueOf(params.get("serialNum"));
        RefundOrder refundOrder = refundOrderMapper.selectBySN(serialNum);
        if (refundOrder == null && serialNum.longValue() != order.getNumber()) {
            log.warn("退款失败任务,退订订单不存在");
            throw new Exception("退订订单不存在");
        }
        refundOrderManager.refundMoney(order, refundOrder == null ? order.getPay_amount() : refundOrder.getMoney(), String.valueOf(refundOrder == null ? order.getNumber() : refundOrder.getNumber()), refundOrder == null ? -1 : refundOrder.getId());
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("退款失败任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "ordCode", // 订单ID
                "serialNum" // 流水号
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        ParamsMapValidate.validate(params, rules);
    }
}
