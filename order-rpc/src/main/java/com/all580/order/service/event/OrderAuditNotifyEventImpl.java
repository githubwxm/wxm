package com.all580.order.service.event;

import com.all580.order.api.model.OrderAuditEventParam;
import com.all580.order.api.service.event.OrderAuditNotifyEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderItem;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Slf4j
@Service
public class OrderAuditNotifyEventImpl  extends BaseNotifyEvent implements OrderAuditNotifyEvent {


    @Autowired
    private OrderItemMapper orderItemMapper;
    @Override
    public Result process(String s, OrderAuditEventParam orderAuditEventParam, Date date) {
        String op_code="ORTHER";
        OrderItem item = orderItemMapper.selectByPrimaryKey(orderAuditEventParam.getItemId());
        Assert.notNull(item);
        if(orderAuditEventParam.isStatus()){
            op_code="AUDIT_SUCCESS";
        }else{
            op_code="AUDIT_FAIL";
        }
        Map<String,Object> map = new HashMap<>();
        map.put("payment_flag",item.getPayment_flag());
        notifyEvent(  orderAuditEventParam.getItemId(), op_code,map);
        return new Result(true);
    }
}
