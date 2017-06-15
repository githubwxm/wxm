package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.manager.MessageManager;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Linv2 on 2017-06-09.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class ResendManager {
    static ValidRule.Pattern pattern = new ValidRule.Pattern(ValidRule.MOBILE_PHONE);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MessageManager messageManager;


    private Order order;

    public ResendManager() {}

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setOrder(String orderCode) {
        this.order = orderMapper.selectByOrderCode(orderCode);
    }

    public void setOrder(Integer orderId) {
        this.order = orderMapper.selectByPrimaryKey(orderId);
    }

    public void setOrder(Integer supplyId, String supplyOrderId) {
        this.order = orderMapper.selectBySupply(supplyId, supplyOrderId);
    }

    public void setOrder(Integer platformId, String platformOrderCode, String seqId) {
        this.order = orderMapper.selectByPlatform(platformId, platformOrderCode, seqId);
    }

    public void send() throws Exception {
        if (order == null) {
            throw new Exception("订单不存在");
        }
        send(order.getMobile());
    }

    public void send(String mobile) throws Exception {
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if (order.getSendType().equals(VoucherConstant.SmsSendType.NO)) {
            throw new Exception("该订单不支持重发操作");
        }
        if (StringUtils.isEmpty(mobile)) {
            mobile = order.getMobile();
        }
        if (!pattern.valid(mobile, null)) {
            throw new Exception("手机号码格式错误");
        }
        messageManager.sendOrderMessage(mobile, order);

    }
}

