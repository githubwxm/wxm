package com.all580.order.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.dto.CanRefundResult;
import com.all580.order.dto.RefundRate;
import com.all580.order.entity.*;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.consts.ProductRules;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;

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
     * 判断是否可退 并更新退票数
     * @param daysMap 每天数据
     * @param detailList 订单详情
     * @throws Exception
     */
    public int canRefundForDays(Map daysMap, List<OrderItemDetail> detailList) throws Exception {
        int total = 0;
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
            total += quantity;
            // 修改退票数
            detail.setRefundQuantity(detail.getRefundQuantity() + quantity);
            orderItemDetailMapper.updateByPrimaryKey(detail);

            // 判断游客余票
            Map visitors = (Map) daysMap.get("visitors");
            int tmpVqty = canVisitorRefund(visitors, day.toString(), detail);
            if (tmpVqty != quantity) {
                throw  new ApiException(String.format("日期:%s游客退票数不符",
                        new Object[]{day}));
            }
        }
        return total;
    }

    /**
     * 判断游客余票退票 并更新预退票数
     * @param visitors 游客退票数据
     * @param day 日期
     * @param detail 订单详情
     */
    private int canVisitorRefund(Map visitors, String day, OrderItemDetail detail) {
        int total = 0;
        List<Visitor> visitorList = visitorMapper.selectByOrderDetailId(detail.getId());
        if (visitorList != null) {
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
                visitor.setPreReturn(vqty);
                visitorMapper.updateByPrimaryKey(visitor);
                total += vqty;
            }
        }
        return total;
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

    /**
     * 计算退支付金额
     * @param detailList 每日订单详情
     * @param itemId 子订单ID
     * @param epId 支付企业ID
     * @param coreEpId 支付企业余额托管平台商ID
     * @return
     */
    public int calcRefundMoney(List<OrderItemDetail> detailList, int itemId, int epId, int coreEpId) {
        OrderItemAccount account = orderItemAccountMapper.selectByOrderItemAndEp(itemId, epId, coreEpId);
        if (account == null) {
            throw new ApiException("数据异常,分账记录不存在");
        }
        String data = account.getData();
        if (StringUtils.isEmpty(data)) {
            throw new ApiException("数据异常,分账记录的每日单价不存在");
        }
        JSONArray daysData = JSONArray.parseArray(data);
        if (daysData.size() != detailList.size()) {
            throw new ApiException("数据异常,分账记录的每日单价与订单详情不匹配");
        }
        int i = 0;
        int money = 0;
        Date refundDate = new Date();
        for (OrderItemDetail detail : detailList) {
            JSONObject dayData = daysData.getJSONObject(i);
            int outPrice = dayData.getIntValue("outPrice");
            Map<String, Integer> rate = ProductRules.calcRefund(detail.getCustRefundRule(), detail.getDay(), refundDate);
            if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
                money += outPrice + rate.get("fixed");
            } else {
                money += money * (rate.get("percent") / 100);
            }
            i++;
        }
        return money;
    }

    /**
     * 生成退订订单
     * @param itemId 子订单ID
     * @param daysMap 每天退票数
     * @param quantity 总退票数
     * @param money 退支付金额
     * @return
     */
    public RefundOrder generateRefundOrder(int itemId, Map daysMap, int quantity, int money) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setOrderItemId(itemId);
        refundOrder.setNumber(UUIDGenerator.generateUUID());
        refundOrder.setQuantity(quantity);
        refundOrder.setData(JsonUtils.toJson(daysMap));
        refundOrder.setStatus(OrderConstant.RefundOrderStatus.AUDIT_WAIT);
        refundOrder.setCreateTime(new Date());
        refundOrder.setMoney(money);
        refundOrderMapper.insert(refundOrder);
        return refundOrder;
    }
}
