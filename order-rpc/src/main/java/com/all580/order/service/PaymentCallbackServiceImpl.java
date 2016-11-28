package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.RefundOrderManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RefundOrderManager refundOrderManager;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result payCallback(long ordCode, String serialNum, String outTransId) {
        log.debug("支付回调:订单号:{};流水:{};交易号:{}", new Object[]{ordCode, serialNum, outTransId});

        DistributedReentrantLock lock = distributedLockTemplate.execute(String.valueOf(ordCode), lockTimeOut);
        try {
            Order order = orderMapper.selectBySN(ordCode);
            if (order == null) {
                log.warn("订单:{}不存在", ordCode);
                return new Result(false, "订单不存在");
            }
            if (order.getStatus() != OrderConstant.OrderStatus.PAYING) {
                log.warn("订单:{}状态:{}不是支付中", ordCode, order.getStatus());
                return new Result(false, "订单已经处理");
            }
            order.setThird_serial_no(outTransId);
            order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
            orderMapper.updateByPrimaryKeySelective(order);

            // 支付成功后加平台商余额(平帐),余额支付不做平帐
            if (order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
                BalanceChangeInfo info = new BalanceChangeInfo();
                info.setEpId(bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(order.getBuy_ep_id())));
                info.setCoreEpId(info.getEpId());
                info.setBalance(order.getPay_amount());
                bookingOrderManager.changeBalances(PaymentConstant.BalanceChangeType.THIRD_PAY_FOR_ORDER, serialNum, info);
            }

            // 支付成功回调 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);

            // 同步数据
            bookingOrderManager.syncOrderPaymentData(order.getId());
            return new Result(true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

        if (order.getStatus() == OrderConstant.OrderStatus.PAID_HANDLING) {
            if (!success) {
                addRefundMoneyJob(ordCode, serialNum);
                return new Result(true);
            }
            // 已支付,处理中(分账失败)退订 直接取消
            // 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            bookingOrderManager.addJob(OrderConstant.Actions.CANCEL_CALLBACK, jobParams);
            return new Result(true);
        }

        refundOrderManager.refundMoneyAfter(Long.valueOf(serialNum), success);
        if (!success) {
            addRefundMoneyJob(ordCode, serialNum);
        }
        return new Result(true);
    }

    private void addRefundMoneyJob(Long ordCode, String serialNum) {
        log.info("退款失败 加入任务处理...");
        // 退款失败回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("ordCode", String.valueOf(ordCode));
        jobParams.put("serialNum", serialNum);
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_MONEY, jobParams, true);
    }
}
