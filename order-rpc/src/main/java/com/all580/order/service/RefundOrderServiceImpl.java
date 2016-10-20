package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.RefundOrderService;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private ThirdPayService thirdPayService;

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

            // 每日订单详情
            List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
            // 每日退票详情
            List daysList = (List) params.get("days");
            // 总退票数量
            Integer quantity = Integer.parseInt(params.get("quantity").toString());
            String cause = params.get("cause").toString();

            if (order.getStatus() == OrderConstant.OrderStatus.PAID) {
                if (daysList == null) {
                    throw new ApiException("缺少退票详情");
                }
                // 判断余票 并修改明细退票数量
                int tmpQuantity = refundOrderManager.canRefundForDays(daysList, detailList);
                if (tmpQuantity != quantity) {
                    throw new ApiException("退票总数与每天退票数不符");
                }
            }

            Date refundDate = new Date();
            // 计算退款金额
            int money = refundOrderManager.calcRefundMoney(daysList, detailList, orderItem.getId(), order.getBuyEpId(),
                    refundOrderManager.getCoreEpId(refundOrderManager.getCoreEpId(order.getBuyEpId())), refundDate);
            // 创建退订订单
            RefundOrder refundOrder = refundOrderManager.generateRefundOrder(orderItem.getId(), daysList, quantity, money, cause);

            // 退订分账
            refundOrderManager.preRefundAccount(daysList, orderItem.getId(), refundOrder.getId(), detailList, refundDate);
        } catch (Exception e) {
            log.error("订单申请退订异常", e);
            throw new ApiException("订单申请退订异常", e);
        }
        return new Result<>(true);
    }

    @Override
    public Result<?> cancelNoSplit(Map params) {
        try {
            Order order = orderMapper.selectBySN(Long.valueOf(params.get("order_sn").toString()));
            if (order == null) {
                throw new ApiException("订单不存在");
            }
            if (order.getStatus() != OrderConstant.OrderStatus.PAID_HANDLING) {
                throw new ApiException("订单不在已支付处理中状态");
            }

            refundOrderManager.refundMoney(order, order.getPayAmount(), null);
        } catch (Exception e) {
            log.error("取消未分账订单异常", e);
            throw new ApiException("取消已支付处理中订单异常", e);
        }
        return new Result<>(true);
    }

    @Override
    public Result<?> audit(Map params) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(params.get("refund_sn").toString()));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.AUDIT_WAIT) {
            throw new ApiException("退订订单不在待审核状态");
        }
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrderItemId());
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        boolean status = Boolean.parseBoolean(params.get("status").toString());
        // 通过
        if (status) {
            if (order.getStatus() == OrderConstant.OrderStatus.PAID) {
                // 调用退票
                refundOrderManager.refundTicket(refundOrder);
            }
            // 没有出票直接调用取消
            return refundOrderManager.cancel(order);
        }
        try {
            refundOrderManager.refundFail(refundOrder);
        } catch (Exception e) {
            log.error("订单退订审核异常", e);
            throw new ApiException("订单退订审核异常", e);
        }
        return new Result<>(true);
    }
}
