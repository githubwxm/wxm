package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.entity.MaSendResponse;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.StringUtils;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Slf4j
@Component
public class BaseNotifyEvent {
    @Autowired
    private TopicPushManager topicPushManager;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private MaSendResponseMapper aSendResponseMapper;

    @Value("${mns.notify.top}")
    private String topicName;
    @Value("${mns.notify.tag.prefix}")
    private String tag;
    private static Map<Integer, Integer> map = new HashMap<>();

    static {
        map.put(OrderConstant.OrderItemStatus.AUDIT_WAIT, 231);//BOOKING
        map.put(OrderConstant.OrderItemStatus.AUDIT_SUCCESS, 232);//CONFIRM
        map.put(OrderConstant.OrderItemStatus.NON_SEND, 233);//NON_SEND
        map.put(OrderConstant.OrderItemStatus.SEND, 234);//SENT
        map.put(1, 235);//RETURNING  //退订中 退票中\n4-退款中
        map.put(1, 236);//RETURNING_WAIT  // 退订待审核
        map.put(1, 239);//COMPLETED// 已完成
        map.put(OrderConstant.OrderItemStatus.CANCEL, 240);//CANCEL

        //定单概要状态
        map.put(OrderConstant.OrderStatus.AUDIT_WAIT, 200);//BOOKING 票据状态
        map.put(OrderConstant.OrderStatus.PAY_WAIT, 201);//NON_PAYMENT
        map.put(1, 202);//PAID // 退订订单 有就算 没有退订就返回202  315
        map.put(1, 203);//PARTIAL_REFUND   // 部分退订  10 使用5  退了3  2张未用，没过期   315
        map.put(1, 204);//REFUND //全部退完 10 5 申请退订  退订未成功   315
        map.put(OrderConstant.OrderStatus.CANCEL, 205);//CANCEL
        map.put(1, 206);//COMPLETED// 以完成  全部用完 或过期5 2 3 且退订成功   315
        map.put(OrderConstant.OrderStatus.PAYING, 207);//PAYING
        map.put(OrderConstant.OrderStatus.PAID_HANDLING, 207);//PAYING
    }

    public Map<String, Object> packaging(int orderItemId, String opCode, Map<String,Object> data) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(orderItemId);
        Assert.notNull(item);
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        return packaging(item, order, opCode, data);
    }

    public Map<String, Object> packaging(long itemNumber, String opCode, Map<String,Object> data) {
        OrderItem item = orderItemMapper.selectBySN(itemNumber);
        Assert.notNull(item);
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        return packaging(item, order, opCode, data);
    }

    public Map<String, Object> packaging(OrderItem item, Order order, String opCode, Map<String,Object> data) {
        Assert.notNull(item);
        Assert.notNull(order);
        int[] status = calcStatus(order, item);
        Map<String, Object> map = new HashMap<>();
        map.put("op_code", opCode);
        map.put("order_id", String.valueOf(order.getNumber()));
        map.put("outer_id", order.getOuter_id());
        map.put("order_item_id", String.valueOf(item.getNumber()));
        map.put("ticket_status", status[1]);
        map.put("order_status", status[0]);
        map.put("ep_id", order.getBuy_ep_id());
        map.put("usd_qty", item.getUsed_quantity());
        map.put("rfd_qty", item.getRefund_quantity());
        map.put("total_usd_qty",item.getUsed_quantity());
        map.put("quantity", item.getQuantity());
        map.put("exp_qty", calcExpQty(item));
        List<MaSendResponse> maSendResponses = aSendResponseMapper.selectByOrderItemId(item.getId());
        List<Map> maResponseMaps = new ArrayList<>();
        if (maSendResponses != null) {
            for (MaSendResponse maSendResponse : maSendResponses) {
                maSendResponse.setId(null);
                maSendResponse.setVisitor_id(null);
                maSendResponse.setCreate_time(null);
                maSendResponse.setEp_ma_id(null);
                maSendResponse.setMa_order_id(null);
                maSendResponse.setOrder_item_id(null);
                maSendResponse.setMa_product_id(null);
                maSendResponse.setUpdate_time(null);
                maResponseMaps.add(CommonUtil.obj2Map(maSendResponse));
            }
        }
        map.put("ma_send_response", maResponseMaps);
        map.put("source_type", order.getSource() == OrderConstant.OrderSourceType.SOURCE_TYPE_B2C ? OrderConstant.OrderSourceType.SOURCE_TYPE_B2C : null);
        map.put("number",String.valueOf(item.getNumber()));
        map.put("payment_flag", item.getPayment_flag());
        if (data != null && !data.isEmpty()) map.putAll(data);
        return map;
    }

    protected void notifyEvent(Integer itemId, String opCode,Map<String,Object> tempMap) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(itemId);
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        if (order.getBuy_operator_id() != 0) {
            log.warn("推送通知: 子訂單:{}, OP:{} 不是OTA不予推送", itemId, opCode);
            return;
        }
        Map<String, Object> map = packaging(item, order, opCode, tempMap);
        String str = JsonUtils.toJson(map);
        log.info("推送通知: 子訂單:{}, OP:{}, 數據:{}", new Object[]{itemId, opCode, str});
        topicPushManager.push(topicName, StringUtils.isEmpty(tag) ? null : tag, str, !StringUtils.isEmpty(tag));
    }

    private int[] calcStatus(Order order, OrderItem item) {
        Integer orderStatus = order.getStatus();
        Integer itemStatus = item.getStatus();
        Integer rfd_qty = item.getRefund_quantity();
        Integer usd_qty = item.getUsed_quantity();//使用数
        Integer quantity = item.getQuantity();//订票总数
        itemStatus = map.get(itemStatus);
        orderStatus = map.get(orderStatus);
        if(itemStatus == null){//子订单状态
            if (rfd_qty == 0) {
                itemStatus = 239;
            } else {
                int ref = refundOrderMapper.selectByItemIdAndStatus(item.getId(), OrderConstant.RefundOrderStatus.AUDIT_WAIT,1);
                if (ref > 0) {
                    itemStatus = 236;//查出有待审核的
                } else {
                    itemStatus = 235;//查出有待审核的
                }
            }
        }
        if(orderStatus == null){//主订单
            if(rfd_qty == 0){
                orderStatus = 202;
            }else{
                int num = refundOrderMapper.selectByItemIdAndStatus(item.getId(), OrderConstant.RefundOrderStatus.REFUND_SUCCESS,1);//获取退订成功的数量
                if((quantity - usd_qty - num) == 0){
                    orderStatus = 206;
                } else {
                    num = refundOrderMapper.selectByItemIdAndStatus(item.getId(), OrderConstant.RefundOrderStatus.REFUND_SUCCESS,2);//获取退订不成功的数量
                    if((quantity - usd_qty - num) == 0){//全部用完 有剩下不成功的
                        orderStatus = 204;
                    } else {
                        orderStatus = 203;//部分退订
                    }

                }
            }
        }
        return new int[]{orderStatus, itemStatus};
    }

    private int calcExpQty(OrderItem item) {
        Date maxDate = orderItemDetailMapper.selectExpiryMaxDate(item.getId());
        if (new Date().after(maxDate))
            return item.getQuantity() - item.getRefund_quantity() - item.getUsed_quantity();//如果票有剩余 没有使用完 过期的就算过期。
        return 0;
    }
}
