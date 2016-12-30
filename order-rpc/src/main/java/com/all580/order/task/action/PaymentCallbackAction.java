package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderId = Integer.parseInt(params.get("orderId"));
        String outTransId = params.get("outTransId");
        String serialNum = params.get("serialNum");

        DistributedReentrantLock lock = distributedLockTemplate.execute(String.valueOf(orderId), lockTimeOut);

        try {
            process(orderId, outTransId, serialNum);
        } finally {
            lock.unlock();
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void process(int orderId, String outTransId, String serialNum) throws Exception {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("支付成功回调,订单不存在");
            throw new Exception("订单不存在");
        }

        if (order.getStatus() != OrderConstant.OrderStatus.PAYING) {
            log.warn("支付成功回调,订单:{}状态:{}不是支付中", order.getNumber(), order.getStatus());
            throw new Exception("订单不在支付中状态");
        }
        order.setThird_serial_no(outTransId);
        order.setStatus(OrderConstant.OrderStatus.PAID_HANDLING); // 已支付,处理中
        orderMapper.updateByPrimaryKeySelective(order);

        // 支付成功后加平台商余额(平帐),余额支付不做平帐
        if (order.getPayment_type() != PaymentConstant.PaymentType.BALANCE.intValue()) {
            BalanceChangeInfo info = new BalanceChangeInfo();
            info.setEp_id(bookingOrderManager.getCoreEpId(bookingOrderManager.getCoreEpId(order.getBuy_ep_id())));
            info.setCore_ep_id(info.getEp_id());
            info.setBalance(order.getPay_amount());
            bookingOrderManager.changeBalances(PaymentConstant.BalanceChangeType.THIRD_PAY_FOR_ORDER, serialNum, info);
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        List<ProductSearchParams> lockParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            lockParams.add(bookingOrderManager.parseParams(orderItem));
        }

        // 加已售
        com.framework.common.Result result = productSalesPlanRPCService.addSoldProductStocks(lockParams);
        if (!result.isSuccess()) {
            log.warn("支付成功加已售失败");
            throw new Exception(result.getError());
        }

        // 子订单状态为未出票
        orderItemMapper.setStatusByOrderId(orderId, OrderConstant.OrderItemStatus.NON_SEND);

        // 分账 到付不分账
        if (order.getPay_amount() > 0) {
            bookingOrderManager.paySplitAccount(order, orderItems);
        }
        order.setStatus(OrderConstant.OrderStatus.PAID);
        orderMapper.updateByPrimaryKeySelective(order);

        // 出票
        // 记录任务
        List<Map<String, String>> jobParams = new ArrayList<>();
        List<Map<String, String>> jobGroupParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Map<String, String> jobParam = new HashMap<>();
            jobParam.put("orderItemId", orderItem.getId().toString());
            // 团队票
            if (orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                    orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM) {
                jobGroupParams.add(jobParam);
                continue;
            }
            jobParams.add(jobParam);
        }
        if (jobParams.size() > 0) {
            bookingOrderManager.addJobs(OrderConstant.Actions.SEND_TICKET, jobParams);
        }
        if (jobGroupParams.size() > 0) {
            bookingOrderManager.addJobs(OrderConstant.Actions.SEND_GROUP_TICKET, jobGroupParams);
        }

        // 同步数据
        bookingOrderManager.syncPaymentSuccessData(orderId);

        // 发送短信
        // TODO: 2016/11/16  目前只支持单子订单发送
        smsManager.sendPaymentSuccess(orderItems.get(0));
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
                "outTransId",
                "serialNum"
        }, new ValidRule[]{new ValidRule.NotNull()});

        ParamsMapValidate.validate(params, rules);
    }
}
