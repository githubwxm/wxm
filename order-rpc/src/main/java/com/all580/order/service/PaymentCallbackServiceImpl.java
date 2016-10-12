package com.all580.order.service;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.BookingOrderManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 支付回调服务
 * @date 2016/10/11 20:01
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class PaymentCallbackServiceImpl implements PaymentCallbackService {
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result payCallback(long ordCode, String serialNum) {
        Order order = orderMapper.selectBySN(ordCode);
        if (order == null) {
            return new Result(false, "订单不存在");
        }
        if (!order.getLocalPaymentSerialNo().equals(serialNum)) {
            return new Result(false, "流水号不匹配");
        }
        order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
        orderMapper.updateByPrimaryKey(order);

        // 支付成功后加平台商余额(平帐)
        BalanceChangeInfo info = new BalanceChangeInfo();
        info.setEpId(bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(order.getBuyEpId())));
        info.setCoreEpId(info.getEpId());
        info.setBalance(order.getPayAmount());
        bookingOrderManager.changeBalances(PaymentConstant.BalanceChangeType.THIRD_PAY_FOR_ORDER, serialNum, info);

        // 支付成功回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderId", order.getId().toString());
        bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);
        return new Result(true);
    }

    @Override
    public Result refundCallback(long ordCode, String serialNum, boolean success) {
        return null;
    }
}
