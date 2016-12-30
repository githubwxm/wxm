package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退款成功任务
 * @date 2016/10/10 20:41
 */
@Component(OrderConstant.Actions.REFUND_MONEY_CALLBACK)
@Slf4j
public class RefundMoneyCallbackAction implements JobRunner {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderId = Integer.parseInt(params.get("orderId"));
        Long ordCode = Long.valueOf(params.get("ordCode"));
        String serialNum = params.get("serialNum");
        boolean success = Boolean.parseBoolean(params.get("success"));

        DistributedReentrantLock lock = distributedLockTemplate.execute(String.valueOf(orderId), lockTimeOut);

        try {
            process(orderId, ordCode, serialNum, success);
        } finally {
            lock.unlock();
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void process(int orderId, Long ordCode, String serialNum, boolean success) throws Exception {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("退款成功回调,订单不存在");
            throw new Exception("订单不存在");
        }

        if (order.getStatus() == OrderConstant.OrderStatus.PAID_HANDLING) {
            if (!success) {
                addRefundMoneyJob(ordCode, serialNum);
                return;
            }
            // 已支付,处理中(分账失败)退订 直接取消
            // 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            bookingOrderManager.addJob(OrderConstant.Actions.CANCEL_CALLBACK, jobParams);
            return;
        }

        refundOrderManager.refundMoneyAfter(Long.valueOf(serialNum), success);
        if (!success) {
            addRefundMoneyJob(ordCode, serialNum);
        }
    }

    private void addRefundMoneyJob(Long ordCode, String serialNum) {
        log.info("退款失败 加入任务处理...");
        // 退款失败回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("ordCode", String.valueOf(ordCode));
        jobParams.put("serialNum", serialNum);
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_MONEY, jobParams, true);
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
                "orderId", // 订单ID
                "ordCode"
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});
        rules.put(new String[]{
                "serialNum"
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{
                "success"
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Boolean()});

        ParamsMapValidate.validate(params, rules);
    }
}
