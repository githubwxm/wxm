package com.all580.order.manager;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.entity.*;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;

    /**
     * 取消订单
     * @param sn 订单编号
     * @return
     */
    @Transactional
    public Result cancel(long sn) {
        Order order = orderMapper.selectBySN(sn);
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

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
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

    /**
     * 判断是否可退
     * @param daysMap 每天数据
     * @param itemId 子订单
     * @throws Exception
     */
    public void canRefundForDays(Map daysMap, int itemId) throws Exception {
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(itemId);
        for (Object day : daysMap.keySet()) {
            Date date = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, day.toString());
            OrderItemDetail detail = getDetailByDay(detailList, date);
            if (detail == null) {
                throw new ApiException(String.format("日期:%s没有订单数据", day));
            }
            Integer quantity = Integer.parseInt(daysMap.get(day).toString());
            if (detail.getQuantity() - detail.getUsedQuantity() - detail.getRefundQuantity() < quantity) {
                throw new ApiException(
                        String.format("日期:%s余票不足,已用:%s,已退:%s",
                        new Object[]{day, detail.getUsedQuantity(), detail.getRefundQuantity()}));
            }

            // 判断游客余票
            List<Visitor> visitorList = visitorMapper.selectByOrderDetailId(detail.getId());
            if (visitorList != null) {
                Map visitors = (Map) daysMap.get("visitors");
                if (visitors == null) {
                    throw new ApiException("缺少游客退票信息");
                }
                for (Object sid : visitors.keySet()) {
                    Visitor visitor = getVisitorBySid(visitorList, sid.toString());
                    if (visitor == null) {
                        throw new ApiException(String.format("日期:%s缺少游客:%s", day, sid));
                    }
                    Integer vqty = Integer.parseInt(visitors.get("quantity").toString());
                    if (visitor.getQuantity() - visitor.getReturnQuantity() < vqty) {
                        throw new ApiException(
                                String.format("日期:%s游客:%s余票不足",
                                        new Object[]{day, sid}));
                    }
                }
            }
        }
    }

    /**
     * 获取某天详情
     * @param detailList 天数据
     * @param day 某一天
     * @return
     */
    private OrderItemDetail getDetailByDay(List<OrderItemDetail> detailList, Date day) {
        if (detailList == null || day == null) {
            return null;
        }
        for (OrderItemDetail detail : detailList) {
            if (detail.getDay().equals(day)) {
                return detail;
            }
        }
        return null;
    }

    /**
     * 获取游客信息
     * @param visitorList 游客数据
     * @param sid 身份证
     * @return
     */
    public Visitor getVisitorBySid(List<Visitor> visitorList, String sid) {
        if (visitorList == null || sid == null) {
            return null;
        }
        for (Visitor visitor : visitorList) {
            if (visitor.getSid().equals(sid)) {
                return visitor;
            }
        }
        return null;
    }

    public RefundOrder generateRefundOrder(int itemId) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setOrderItemId(itemId);
        refundOrder.setNumber(UUIDGenerator.generateUUID());
        return refundOrder;
    }
}
