package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.Visitor;
import com.all580.product.api.consts.ProductConstants;
import com.all580.voucher.api.conf.VoucherConstant;
import com.all580.voucher.api.model.SendTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 出票任务
 * @date 2016/10/25 11:30
 */
@Component(OrderConstant.Actions.SEND_TICKET)
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class SendTicketAction implements JobRunner {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private VoucherRPCService voucherRPCService;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderItemId = Integer.parseInt(params.get("orderItemId"));
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(orderItemId);
        if (orderItem == null) {
            log.warn("出票任务,子订单不存在");
            throw new Exception("子订单不存在");
        }

        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        OrderItemDetail detail = detailList.get(0); // 景点只有一天
        SendTicketParams sendTicketParams = new SendTicketParams();
        sendTicketParams.setOrderSn(orderItem.getNumber());
        sendTicketParams.setProductSn(orderItem.getProSubNumber());
        sendTicketParams.setPaymentType(orderItem.getPaymentFlag() == ProductConstants.PayType.PREPAY ?
                VoucherConstant.PaymentType.ONLINE : VoucherConstant.PaymentType.LIVE);
        sendTicketParams.setConsumeType(VoucherConstant.ConsumeType.COUNT); // 默认
        sendTicketParams.setValidTime(detail.getEffectiveDate());
        sendTicketParams.setInvalidTime(detail.getExpiryDate());
        sendTicketParams.setDisableWeek(detail.getDisableWeek());
        sendTicketParams.setDisableDate(detail.getDisableDay());
        // TODO: 2016/11/3 出票发送短信
        sendTicketParams.setSendSms(true);
        //sendTicketParams.setSms("");

        List<Visitor> visitorList = visitorMapper.selectByOrderDetailId(detail.getId());
        List<com.all580.voucher.api.model.Visitor> contacts = new ArrayList<>();
        for (Visitor visitor : visitorList) {
            com.all580.voucher.api.model.Visitor v = new com.all580.voucher.api.model.Visitor();
            BeanUtils.copyProperties(visitor, v);
            contacts.add(v);
        }
        sendTicketParams.setVisitors(contacts);
        com.framework.common.Result r = voucherRPCService.sendTicket(orderItem.getEpMaId(), sendTicketParams);
        if (!r.isSuccess()) {
            log.warn("子订单:{},出票失败:{}", orderItem.getNumber(), r.getError());
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("出票任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "orderItemId" // 子订单ID
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        ParamsMapValidate.validate(params, rules);
    }
}
