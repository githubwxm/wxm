package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
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
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class PaymentCallbackAction implements JobRunner {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderId = Integer.parseInt(params.get("orderId"));
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("支付成功回调,订单不存在");
            throw new Exception("订单不存在");
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        List<ProductSearchParams> lockParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            lockParams.add(bookingOrderManager.parseParams(orderItem));
        }

        // 加已售
        com.framework.common.Result result = productSalesPlanRPCService.addSoldProductStocks(lockParams);
        if (result.hasError()) {
            log.warn("支付成功加已售失败");
            throw new Exception(result.getError());
        }

        // 子订单状态为未出票
        orderItemMapper.setStatusByOrderId(orderId, OrderConstant.OrderItemStatus.NON_SEND);

        // 分账 到付不分账
        if (order.getPayAmount() > 0) {
            bookingOrderManager.paySplitAccount(orderId, orderItems);
        }
        order.setStatus(OrderConstant.OrderStatus.PAID);
        orderMapper.updateByPrimaryKey(order);

        // 出票
        // 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderId", order.getId().toString());
        bookingOrderManager.addJob(OrderConstant.Actions.SEND_TICKET, jobParams);

        // 同步数据
        bookingOrderManager.syncPaymentSuccessData(orderId);
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
