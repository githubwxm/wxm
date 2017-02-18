package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.LockTransactionManager;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付成功任务
 * @date 2016/10/10 20:41
 */
@Component(OrderConstant.Actions.PAYMENT_CALLBACK)
@Slf4j
public class PaymentCallbackAction implements JobRunner {
    @Autowired
    private LockTransactionManager lockTransactionManager;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderId = Integer.parseInt(params.get("orderId"));
        String orderSn = params.get("orderSn");
        String outTransId = params.get("outTransId");
        String serialNum = params.get("serialNum");

        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null && StringUtils.isNotEmpty(orderSn)) {
            order = orderMapper.selectBySN(Long.valueOf(orderSn));
            Assert.notNull(order, "订单不存在");
            orderId = order.getId();
        }

        DistributedReentrantLock lock = distributedLockTemplate.execute(String.valueOf(orderId), lockTimeOut);

        try {
            lockTransactionManager.paymentCallback(orderId, outTransId, serialNum);
        } finally {
            lock.unlock();
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
        rules.put(new String[]{
                "serialNum"
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
