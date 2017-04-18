package com.all580.order.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.LockTransactionManager;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.lang.JsonUtils;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付回调服务
 * @date 2016/10/11 20:01
 */
@Service
@Slf4j
public class PaymentCallbackServiceImpl implements PaymentCallbackService {
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private LockTransactionManager lockTransactionManager;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Autowired
    private JobAspect jobManager;

    @Override
    @JobTask
    public Result payCallback(long ordCode, String serialNum, String outTransId) {
        log.debug("支付回调:订单号:{};流水:{};交易号:{}", new Object[]{ordCode, serialNum, outTransId});

        int orderId = -1;
        String lockKey;
        Order order = orderMapper.selectBySN(ordCode);
        if (order == null) {
            log.warn("订单:{}不存在", ordCode);
            lockKey = String.valueOf(ordCode);
        } else {
            orderId = order.getId();
            lockKey = String.valueOf(orderId);
        }
        log.info("order {} {} {} {} {} {} {} {} {}", new Object[]{
                JsonUtils.toJson(new Date()),
                ordCode,
                null,
                OrderConstant.LogOperateCode.SYSTEM,
                0,
                "PAYMENT",
                OrderConstant.LogOperateCode.PAID_SUCCESS,
                0,
                String.format("订单支付回调:流水:%s,交易号:%s", serialNum, outTransId)
        });

        DistributedReentrantLock lock = distributedLockTemplate.execute(lockKey, lockTimeOut);

        try {
            lockTransactionManager.paymentCallback(orderId, outTransId, serialNum);
        } catch (Exception e) {
            log.warn("支付回调异常,添加任务重试", e);
            // 支付成功回调 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", String.valueOf(orderId));
            jobParams.put("orderSn", String.valueOf(ordCode));
            jobParams.put("outTransId", outTransId);
            jobParams.put("serialNum", serialNum);
            jobManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, Collections.singleton(jobParams));
        } finally {
            lock.unlock();
        }
        return new Result(true);
    }

    @Override
    @JobTask
    public Result refundCallback(Long ordCode, String serialNum, String outTransId, boolean success) {
        log.debug("退款回调-{}:订单号:{};流水:{};交易号:{}", new Object[]{success, ordCode, serialNum, outTransId});
        Order order = orderMapper.selectBySN(ordCode);
        if (order == null && outTransId != null) {
            order = orderMapper.selectByThirdSn(outTransId);
        }
        if (order == null && outTransId == null) {
            order = orderMapper.selectByRefundSn(ordCode);
        }

        if (order == null) {
            log.warn("退款回调:订单不存在");
            return new Result(false, "订单号不存在");
        }

        log.info("order {} {} {} {} {} {} {} {} {}", new Object[]{
                JsonUtils.toJson(new Date()),
                order.getNumber(),
                null,
                OrderConstant.LogOperateCode.SYSTEM,
                0,
                "PAYMENT",
                success ? OrderConstant.LogOperateCode.REFUND_MONEY_SUCCESS : OrderConstant.LogOperateCode.REFUND_MONEY_FAIL,
                0,
                String.format("订单退款回调:流水:%s,交易号:%s", serialNum, outTransId)
        });

        DistributedReentrantLock lock = distributedLockTemplate.execute(String.valueOf(order.getId()), lockTimeOut);

        try {
            lockTransactionManager.refundMoneyCallback(order.getId(), ordCode, serialNum, success);
        } catch (Exception e) {
            log.warn("退款回调异常,添加任务重试", e);
            // 退款成功回调 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            jobParams.put("ordCode", String.valueOf(ordCode));
            jobParams.put("serialNum", serialNum);
            jobParams.put("success", Boolean.toString(success));
            jobManager.addJob(OrderConstant.Actions.REFUND_MONEY_CALLBACK, Collections.singleton(jobParams));
        } finally {
            lock.unlock();
        }

        return new Result(true);
    }
}
