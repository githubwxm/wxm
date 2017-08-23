package com.all580.order.service.event;

import com.all580.order.api.service.event.SendTicketMsgEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description:
 * @date 17-7-31 下午2:08
 */
@Service
@Slf4j
public class SendTicketMsgEventImpl implements SendTicketMsgEvent {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private SmsManager smsManager;

    @Override
    public Result process(String s, Integer integer, Date date) {
        OrderItem item = orderItemMapper.selectByPrimaryKey(integer);
        Assert.notNull(item);
        if (item.getVoucher_template() == null || item.getVoucher_template() == 0) {
            log.warn("订单没有凭证短信模板,短信发送失败");
            return new Result(true);
        }

        if ((item.getSend() == null || item.getSend())) {
            smsManager.sendVoucherMsg(item);
        }
        return new Result(true);
    }
}
