package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.service.event.BasicSyncDataEvent;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 出票任务
 * @date 2016/10/25 11:30
 */
@Component(OrderConstant.Actions.SEND_TICKET)
@Slf4j
public class SendTicketAction extends BasicSyncDataEvent implements JobRunner {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private VoucherRPCService voucherRPCService;

    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderItemId = Integer.parseInt(params.get("orderItemId"));
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(orderItemId);
        if (orderItem == null) {
            log.warn("出票任务,子订单不存在");
            throw new Exception("子订单不存在");
        }
        if (orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() != ProductConstants.TeamTicketType.INDIVIDUAL) {
            log.warn("出票任务,该订单不是散客订单");
            throw new Exception("该订单不是散客订单");
        }
        orderItem.setStatus(OrderConstant.OrderItemStatus.TICKETING);
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        OrderItemDetail detail = detailList.get(0); // 景点只有一天
        SendTicketParams sendTicketParams = new SendTicketParams();
        sendTicketParams.setOrderSn(orderItem.getNumber());
        sendTicketParams.setProductSn(orderItem.getPro_sub_number());
        sendTicketParams.setPaymentType(orderItem.getPayment_flag() == ProductConstants.PayType.PREPAY ?
                VoucherConstant.PaymentType.ONLINE : VoucherConstant.PaymentType.LIVE);
        sendTicketParams.setConsumeType(VoucherConstant.ConsumeType.COUNT); // 默认
        sendTicketParams.setValidTime(detail.getEffective_date());
        sendTicketParams.setInvalidTime(detail.getExpiry_date());
        sendTicketParams.setDisableWeek(detail.getDisable_week());
        sendTicketParams.setDisableDate(detail.getDisable_day());
        sendTicketParams.setMaProductId(orderItem.getMa_product_id());
        // TODO: 2016/11/3 出票发送短信
        sendTicketParams.setSendSms(true);
        sendTicketParams.setSms(orderItem.getVoucher_msg());

        List<Visitor> visitorList = visitorMapper.selectByOrderItem(orderItemId);
        List<com.all580.voucher.api.model.Visitor> contacts = new ArrayList<>();
        for (Visitor visitor : visitorList) {
            com.all580.voucher.api.model.Visitor v = new com.all580.voucher.api.model.Visitor();
            BeanUtils.copyProperties(visitor, v);
            if (StringUtils.isEmpty(v.getSid())) {
                v.setSid("110101194901013454");
            }
            contacts.add(v);
        }
        sendTicketParams.setVisitors(contacts);
        com.framework.common.Result r = voucherRPCService.sendTicket(orderItem.getEp_ma_id(), sendTicketParams);
        log.info(OrderConstant.LogOperateCode.NAME, bookingOrderManager.orderLog(null, orderItem.getId(),
                0, "ORDER_ACTION", OrderConstant.LogOperateCode.SEND_TICKETING,
                orderItem.getQuantity(), String.format("散客出票任务:发送状态:%s", String.valueOf(r.isSuccess()))));

        if (!r.isSuccess()) {
            log.warn("子订单:{},出票失败:{}", orderItem.getNumber(), r.getError());
            throw new Exception("出票失败:" + r.getError());
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order_item", orderItemMapper.selectByPrimaryKey(orderItem.getId()));
        syncAccess.loop();
        sync(syncAccess.getDataMaps());
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
