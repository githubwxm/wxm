package com.all580.order.service.event;

import com.all580.order.api.service.event.RefundSuccessEvent;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.SmsManager;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/5 11:26
 */
@Service
public class RefundSuccessEventImpl implements RefundSuccessEvent {
    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private SmsManager smsManager;

    @Override
    public Result process(String msgId, Integer content, Date createDate) {
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(content);
        Assert.notNull(refundOrder, "退订订单不存在");

        // 发送短信 退订
        smsManager.sendRefundSuccessSms(refundOrder);
        return new Result(true);
    }
}
