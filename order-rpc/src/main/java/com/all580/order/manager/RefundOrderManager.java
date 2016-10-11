package com.all580.order.manager;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订
 * @date 2016/10/9 10:32
 */
@Component
@Slf4j
public class RefundOrderManager extends BaseOrderManager {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;

    /**
     * 取消订单
     * @param orderId 订单ID
     * @return
     */
    public Result cancel(int orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        // 检查订单状态
        switch (order.getStatus()) {
            case OrderConstant.OrderStatus.CANCEL:
                throw new ApiException("重复操作,订单已取消");
            case OrderConstant.OrderStatus.PAID:
            case OrderConstant.OrderStatus.PAID_HANDLING:
                throw new ApiException("订单已支付,请走退订流程");
            case OrderConstant.OrderStatus.PAYING:
                throw new ApiException("支付中,不能取消");
            default:
                order.setStatus(OrderConstant.OrderStatus.CANCEL);
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        List<ProductSearchParams> lockParams = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND ||
                    orderItem.getStatus() == OrderConstant.OrderItemStatus.TICKETING) {
                throw new ApiException("已出票或正在出票中,不能取消");
            }

            orderItem.setStatus(OrderConstant.OrderItemStatus.CANCEL);
            orderItemMapper.updateByPrimaryKey(orderItem);
            lockParams.add(parseParams(orderItem));
        }
        // 更新主订单为已取消
        orderMapper.updateByPrimaryKey(order);
        // 还库存
        Result result = productSalesPlanRPCService.addProductStocks(lockParams);
        if (result.hasError()) {
            throw new ApiException(result.getError());
        }
        return new Result(true);
    }
}
