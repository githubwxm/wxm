package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.entity.MaSendResponse;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.StringUtils;
import com.framework.common.mns.TopicPushManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        map.put(320, 231);//BOOKING
        map.put(321, 232);//CONFIRM
        map.put(322, 233);//NON_SEND
        map.put(325, 234);//SENT
        map.put(1, 235);//RETURNING  //退订中 退票中\n4-退款中
        map.put(1, 236);//RETURNING_WAIT  // 退订待审核
        map.put(1, 239);//COMPLETED// 已完成
        map.put(326, 240);//CANCEL

//        public static final int AUDIT_WAIT = 320; // 待审核
//        public static final int AUDIT_SUCCESS = 321; // 审核通过
//        //public static final int NON_SEND = 322; // 未出票
//        public static final int TICKET_FAIL = 323; // 出票失败
//        public static final int TICKETING = 324; // 出票中
//        //public static final int SEND = 325; // 已出票
//        //public static final int CANCEL = 326; // 已取消
//        public static final int MODIFYING = 327; // 修改中
//        public static final int MODIFY = 328; // 已修改
//        public static final int MODIFY_FAIL = 329; // 修改失败
        //定单概要状态
        map.put(310, 200);//BOOKING 票据状态
        map.put(311, 201);//NON_PAYMENT
        map.put(1, 202);//PAID // 退订订单 有就算 没有退订就返回202  315
        map.put(1, 203);//PARTIAL_REFUND   // 部分退订  10 使用5  退了3  2张未用，没过期   315
        map.put(1, 204);//REFUND //全部退完 10 5 申请退订  退订未成功   315
        map.put(316, 205);//CANCEL
        map.put(1, 206);//COMPLETED// 以完成  全部用完 或过期5 2 3 且退订成功   315
        map.put(312, 207);//PAYING
        map.put(314, 207);//PAYING
//        public static final int AUDIT_WAIT = 310; // 待审核
//        public static final int PAY_WAIT = 311; // 待支付
//        public static final int PAYING = 312; // 支付中
//        public static final int PAY_FAIL = 313; // 支付失败
//        public static final int PAID_HANDLING = 314; // 已支付,处理中
//        public static final int PAID = 315; // 已支付,交易成功
//        public static final int CANCEL = 316; // 已取消

    }

    protected void notifyEvent(Integer itemId, String opCode,Map<String,Object> tempMap) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(itemId);
        Assert.notNull(item);
        Integer rfd_qty= refundOrderMapper.selectItemQuantity(itemId);
        Order order = orderMapper.selectByPrimaryKey(item.getOrder_id());
        Assert.notNull(order);
        if(order.getBuy_operator_id()!=0){
            log.info("通知事物数据: 操作人id:{} orderid:{} " , order.getBuy_operator_id(),order.getId());
            return;
        }
        Integer usd_qty = item.getUsed_quantity();//使用数
        //Integer rfd_qty = item.getRefund_quantity();// 退票数
        Integer quantity = item.getQuantity();//订票总数
        Integer exp_qty = 0;//过期数
        String MaxDate = orderItemDetailMapper.selectExpiryMaxDate(itemId);
        Date date = DateFormatUtils.converToDateTime(MaxDate);
        Date currentDate = new Date();
        if (currentDate.after(date)) {
            exp_qty=quantity - rfd_qty - usd_qty;//如果票有剩余 没有使用完 过期的就算过期。
        }
        Integer orderStatus=order.getStatus();
        Integer itemStatus=item.getStatus();
        if(map.get(itemStatus)==null){//子订单状态
             if(rfd_qty==0){
                 itemStatus=239;//
             }else{
                int ref= refundOrderMapper.selectByItemIdAndStatus(itemId, OrderConstant.RefundOrderStatus.AUDIT_WAIT,1);
                 if(ref>0){
                     itemStatus= 236;//查出有待审核的
                 }else{
                     itemStatus= 235;//查出有待审核的
                 }
             }
        }else{
            itemStatus=map.get(itemStatus);
        }
        if(map.get(orderStatus)==null){//主订单
            if(rfd_qty==0){
                orderStatus=202;//
            }else{//
               int num= refundOrderMapper.selectByItemIdAndStatus(itemId,
                       OrderConstant.RefundOrderStatus.REFUND_SUCCESS,1);//获取退订成功的数量
                if((quantity-usd_qty-num)==0){
                    orderStatus=206;
                }else{
                    num= refundOrderMapper.selectByItemIdAndStatus(itemId,
                            OrderConstant.RefundOrderStatus.REFUND_SUCCESS,2);//获取退订不成功的数量
                    if((quantity-usd_qty-num)==0){//全部用完 有剩下不成功的
                        orderStatus=204;
                    }else{
                        orderStatus=203;//部分退订
                    }

                }
            }
        }else{
            orderStatus=map.get(orderStatus);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("op_code", opCode);
        map.put("order_id", order.getNumber());
        map.put("order_item_id", item.getNumber());
        map.put("ticket_status", itemStatus);
        map.put("order_status", orderStatus);
        map.put("ep_id", order.getBuy_ep_id());
        map.put("usd_qty",usd_qty );
        map.put("rfd_qty", rfd_qty);
        map.put("quantity", quantity);
        map.put("exp_qty", exp_qty);
        List<MaSendResponse>list =aSendResponseMapper.selectByOrderItemId(itemId);
        if(null==list||list.isEmpty()){
            map.put("ma_send_response","");
        }else{
            MaSendResponse maSendResponse =list.get(0);
            map.put("ma_send_response",maSendResponse.getImage_url());
        }
        map.put("number",item.getNumber());
        if(tempMap!=null){
            map.putAll(tempMap);
        }
      String str = JsonUtils.toJson(map);
        log.info("通知事物数据: " + str);
        topicPushManager.push(topicName, StringUtils.isEmpty(tag) ? null : tag, str, !StringUtils.isEmpty(tag));
    }
}
