package com.all580.order.service.event;

import com.all580.order.api.model.ConsumeTicketEventParam;
import com.all580.order.api.service.event.ConsumeTicketNotifyEvent;
import com.all580.order.api.service.event.SendTicketNotifyEvent;
import com.all580.order.dao.OrderClearanceSerialMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderClearanceSerial;
import com.all580.order.entity.OrderItem;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/2/16 0016.
 */
@Service
public class ConsumeTicketNotifyEventImpl extends BaseNotifyEvent implements ConsumeTicketNotifyEvent {


//    @Autowired
//    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Override
    public Result process(String s, ConsumeTicketEventParam content, Date date) {
//        OrderItem item = orderItemMapper.selectByPrimaryKey(content.getItemId());
//        Assert.notNull(item);
        OrderClearanceSerial serial=orderClearanceSerialMapper.selectByPrimaryKey(content.getSerialId()) ;
        Map<String,Object> map = new HashMap<>();
        map.put("id",serial.getSerial_no());
        map.put("created",serial.getClearance_time());
        map.put("consume_amount",serial.getQuantity());
        map.put("vocher_remaining","");
        map.put("device_sid","");
        map.put("total_usd_qty",serial.getQuantity());

        Map<String,Object> consumeInfo = new HashMap<>();
        consumeInfo.put("consume_info",map);
        notifyEvent(content.getItemId(), "CONSUME",consumeInfo);
        return new Result(true);


//        private String id;//(消费ID),
//        private String created;//(消费时间),
//        private String consume_amount;//(本次消费数),
//        private String vocher_remaining;//(本次消费后剩余数),
//        private String device_sid;//(核销设备号)
    }
}
