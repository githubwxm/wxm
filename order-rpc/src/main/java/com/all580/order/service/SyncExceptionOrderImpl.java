package com.all580.order.service;

import com.all580.order.api.service.SyncExceptionOrder;
import com.all580.order.dao.*;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.service.event.BasicSyncDataEvent;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2017/3/31 0031.
 */
@Service
@Slf4j
public class SyncExceptionOrderImpl extends BasicSyncDataEvent implements SyncExceptionOrder {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;
   @Autowired
   private RefundOrderMapper refundOrderMapper;
    @Autowired
    private RefundAccountMapper refundAccountMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private OrderClearanceDetailMapper orderClearanceDetailMapper;

    @Autowired
    private OrderExceptionMapper orderExceptionMapper;



    public Result selectOrderException(Map<String,Object> map){
        Map<String,Object> resultMap = new HashMap();
        CommonUtil.checkPage(map);
        List<Map<String,Object>> list= orderExceptionMapper.selectOrderException(map);
        resultMap.put("list",list);
        resultMap.put("totalCount",orderExceptionMapper.selectOrderExceptionCount(map));
        Result result = new Result(true);
        result.put(resultMap);
        return result;
    }


    public Result selectSyncOrder(Long sn){
        List<Map<String,Object>> list =orderMapper.selectByNumberItem(sn);
        if( null==list|| list.isEmpty()){
            throw new ApiException("未找到主订单");
        }
        Map<String,Object> map = new HashMap<>();
        Result result = new Result(true);
        map.put("list",list);
        result.put(map);
        return result;
    }

    public Result SyncOrderAll(Long sn){
        Order order= orderMapper.selectBySN(sn);
        SyncAccess syncAccess = getAccessKeys(order);
        List<OrderItem>  orderItemList = orderItemMapper.selectByOrderId(order.getId());
        syncAccess.getDataMap().add("t_order", order)
                .add("t_shipping", shippingMapper.selectByOrder(order.getId()));
        for(OrderItem orderItem :orderItemList){
            Integer itemId = orderItem.getId();
            syncAccess.getDataMap()
                    .add("t_order_item", orderItem)
                    .add("t_order_item_account", orderItemAccountMapper.selectByOrderItem(itemId))
                    .add("t_order_item_detail", orderItemDetailMapper.selectByItemId(itemId))
                    .add("t_ma_send_response", maSendResponseMapper.selectByOrderItemId(itemId))
                    .add("t_refund_order", refundOrderMapper.selectByItemId(itemId))
                    .add("t_refund_account", refundAccountMapper.selectItemRefundOrder(itemId))
                    .add("t_refund_serial", refundSerialMapper.selectItemRefundSerial(itemId))
                    .add("t_refund_visitor", refundVisitorMapper.selectByItemId(itemId))
                    .add("t_visitor", visitorMapper.selectByOrderItem(itemId))
                    .add("t_order_clearance_serial", orderClearanceSerialMapper.selectItemClearanceSerial(itemId))
                    .add("t_order_clearance_detail", orderClearanceDetailMapper.selectItemClearanceDetail(itemId))
            ;
        }

        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(true);
    }
}
