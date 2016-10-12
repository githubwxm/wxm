package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.RefundOrderService;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订服务
 * @date 2016/10/11 10:49
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class RefundOrderServiceImpl implements RefundOrderService {
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;

    @Override
    public Result<?> cancel(Map params) {
        return refundOrderManager.cancel(Long.valueOf(params.get("order_sn").toString()));
    }

    @Override
    public Result<?> apply(Map params) {
        try {
            OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(params.get("order_item_sn").toString()));
            if (orderItem == null) {
                throw new ApiException("订单不存在");
            }
            Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            if (Arrays.binarySearch(new int[]{
                    OrderConstant.OrderItemStatus.SEND,
                    OrderConstant.OrderItemStatus.NON_SEND,
                    OrderConstant.OrderItemStatus.TICKET_FAIL
            }, orderItem.getStatus()) < 0 ||
                    Arrays.binarySearch(new int[]{
                            OrderConstant.OrderStatus.PAY_FAIL,
                            OrderConstant.OrderStatus.PAID,
                            OrderConstant.OrderStatus.PAID_HANDLING
                    }, order.getStatus()) < 0) {
                throw new ApiException("订单不在可退订状态");
            }

            // 判断余票 并修改明细退票数量
            Map daysMap = (Map) params.get("days");
            int tmpQuantity = refundOrderManager.canRefundForDays(daysMap, orderItem.getId());
            Integer quantity = Integer.parseInt(params.get("quantity").toString());
            if (tmpQuantity != quantity) {
                throw new ApiException("退票总数与每天退票数不符");
            }

            // 创建退订订单
        } catch (Exception e) {
            log.error("订单申请退订异常", e);
            throw new ApiException("订单申请退订异常", e);
        }
        return null;
    }

    @Override
    public Result<?> audit(Map params) {
        return null;
    }
}
