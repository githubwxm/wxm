package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.ConsumeTicketInfo;
import com.all580.order.api.model.ReConsumeTicketInfo;
import com.all580.order.api.model.RefundTicketInfo;
import com.all580.order.api.model.SendTicketInfo;
import com.all580.order.api.service.TicketCallbackService;
import com.all580.order.dao.*;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 票务凭证回调服务
 * @date 2016/10/15 9:50
 */
@Service
@Slf4j
public class TicketCallbackServiceImpl implements TicketCallbackService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private OrderClearanceDetailMapper orderClearanceDetailMapper;
    @Autowired
    private ClearanceWashedSerialMapper clearanceWashedSerialMapper;
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Override
    public Result sendTicket(Long orderSn, List<SendTicketInfo> infoList, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }
        if (orderItem.getStatus() != OrderConstant.OrderItemStatus.TICKETING) {
            return new Result(false, "订单状态不在出票中");
        }
        orderItem.setStatus(OrderConstant.OrderItemStatus.SEND);
        orderItem.setSendMaTime(procTime);
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        List<MaSendResponse> maSendResponseList = maSendResponseMapper.selectByOrderItemId(orderItem.getId());
        for (SendTicketInfo ticketInfo : infoList) {
            // check
            if (getMaSendResponse(maSendResponseList, ticketInfo.getVisitorSeqId(), orderItem.getEpMaId()) != null) {
                continue;
            }
            MaSendResponse response = new MaSendResponse();
            response.setVisitorId(ticketInfo.getVisitorSeqId()); // 游客ID
            response.setEpMaId(orderItem.getEpMaId()); // 哪个凭证的商户ID
            response.setMaOrderId(ticketInfo.getTicketId()); // 凭证号对应的凭证ID
            response.setSid(ticketInfo.getSid()); // 身份证
            response.setPhone(ticketInfo.getPhone()); // 手机号码
            response.setImageUrl(ticketInfo.getImgUrl()); // 二维码
            response.setVoucherValue(ticketInfo.getVoucherNumber()); // 凭证号
            response.setMaProductId(ticketInfo.getMaProductId()); // 凭证产品ID
            response.setOrderItemId(orderItem.getId()); // 子订单ID
            response.setCreateTime(procTime);
            maSendResponseMapper.insertSelective(response);
        }

        // 同步数据
        bookingOrderManager.syncSendTicketData(orderItem.getId());
        return new Result(true);
    }

    @Override
    public Result consumeTicket(Long orderSn, ConsumeTicketInfo info, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        // 获取订单详情 设置核销数量
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        if (detailList == null || detailList.size() < 1) {
            return new Result(false, "订单详情不存在");
        }

        OrderClearanceSerial oldSerial = orderClearanceSerialMapper.selectBySn(info.getValidateSn());
        if (oldSerial != null) {
            return new Result(false, "核销流水:" + info.getValidateSn() + "重复核销");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        itemDetail.setUsedQuantity(itemDetail.getUsedQuantity() + info.getConsumeQuantity());
        orderItemDetailMapper.updateByPrimaryKeySelective(itemDetail);

        // 保存核销流水
        OrderClearanceSerial serial = new OrderClearanceSerial();
        serial.setOrderItemId(orderItem.getId());
        serial.setClearanceTime(procTime);
        serial.setCreateTime(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(info.getConsumeQuantity());
        serial.setSerialNo(info.getValidateSn());
        orderClearanceSerialMapper.insertSelective(serial);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        // 保存核销明细
        OrderClearanceDetail detail = new OrderClearanceDetail();
        detail.setSerialNo(info.getValidateSn());
        detail.setName(visitor.getName());
        detail.setSid(visitor.getSid());
        detail.setPhone(visitor.getPhone());
        orderClearanceDetailMapper.insertSelective(detail);

        // 设置核销人核销数量
        visitor.setUseQuantity(visitor.getUseQuantity() + info.getConsumeQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);

        // 发送短信
        smsManager.sendConsumeSms(orderItem, info.getConsumeQuantity());

        // 分账
        // 核销成功 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerialNo());
        refundOrderManager.addJob(OrderConstant.Actions.CONSUME_SPLIT_ACCOUNT, jobParams);

        // 同步数据
        bookingOrderManager.syncConsumeData(orderItem.getId(), info.getValidateSn());
        return new Result(true);
    }

    @Override
    public Result reConsumeTicket(Long orderSn, ReConsumeTicketInfo info, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        OrderClearanceSerial orderClearanceSerial = orderClearanceSerialMapper.selectBySn(info.getValidateSn());
        if (orderClearanceSerial == null) {
            return new Result(false, "核销流水不存在");
        }

        // 获取订单详情 设置核销数量
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        if (detailList == null || detailList.size() < 1) {
            return new Result(false, "订单详情不存在");
        }

        ClearanceWashedSerial oldSerial = clearanceWashedSerialMapper.selectBySn(info.getReValidateSn());
        if (oldSerial != null) {
            return new Result(false, "反核销流水:" + info.getReValidateSn() + "重复冲正");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        itemDetail.setUsedQuantity(itemDetail.getUsedQuantity() - orderClearanceSerial.getQuantity());
        orderItemDetailMapper.updateByPrimaryKeySelective(itemDetail);

        // 保存冲正流水
        ClearanceWashedSerial serial = new ClearanceWashedSerial();
        serial.setSerialNo(info.getReValidateSn());
        serial.setClearanceSerialNo(info.getValidateSn());
        serial.setClearanceWashedTime(procTime);
        serial.setCreateTime(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(orderClearanceSerial.getQuantity());
        clearanceWashedSerialMapper.insertSelective(serial);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        // 设置核销人核销数量
        visitor.setUseQuantity(visitor.getUseQuantity() - orderClearanceSerial.getQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);

        // 发送短信
        smsManager.sendReConsumeSms(orderItem, orderClearanceSerial.getQuantity(), orderClearanceSerial.getQuantity());

        // 分账
        // 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerialNo());
        refundOrderManager.addJob(OrderConstant.Actions.RE_CONSUME_SPLIT_ACCOUNT, jobParams);

        // 同步数据
        bookingOrderManager.syncReConsumeData(orderItem.getId(), info.getReValidateSn());
        return new Result(true);
    }

    @Override
    public Result refundTicket(Long orderSn, RefundTicketInfo info, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        RefundSerial refundSerial = refundSerialMapper.selectByLocalSn(Long.valueOf(info.getRefId()));
        if (refundSerial == null) {
            return new Result(false, "退票流水错误");
        }

        if (refundSerial.getRefundTime() != null) {
            return new Result(false, "退票流水:" + info.getRefId() + "重复操作");
        }

        RefundOrder refundOrder = refundOrderMapper.selectByItemIdAndRefundSn(orderItem.getId(), Long.valueOf(info.getRefId()));
        if (refundOrder == null) {
            return new Result(false, "退订订单不存在");
        }

        // 退票失败
        if (!info.isSuccess()) {
            try {
                refundOrderManager.refundFail(refundOrder);
            } catch (Exception e) {
                throw new ApiException("退票失败还原状态异常", e);
            }

            // 发送短信
            smsManager.sendRefundFailSms(orderItem);

            refundOrderManager.syncRefundOrderAuditRefuse(refundOrder.getId());
            return new Result(true);
        }

        refundOrder.setStatus(OrderConstant.RefundOrderStatus.REFUND_MONEY); // 退款中
        refundOrder.setRefundTicketTime(procTime);

        //refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefundTime(procTime);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        visitor.setReturnQuantity(visitor.getReturnQuantity() + refundSerial.getQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);

        RefundVisitor refundVisitor = refundVisitorMapper.selectByRefundIdAndVisitorId(refundOrder.getId(), info.getVisitorSeqId());
        refundVisitor.setReturnQuantity(refundVisitor.getReturnQuantity() + refundSerial.getQuantity());
        refundVisitor.setPreQuantity(0);
        refundVisitorMapper.updateByPrimaryKeySelective(refundVisitor);
        orderItem.setRefundQuantity(orderItem.getRefundQuantity() + refundSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        refundOrderMapper.updateByPrimaryKeySelective(refundOrder);
        refundSerialMapper.updateByPrimaryKeySelective(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        // 支付宝退款走财务手动
        if (order.getPaymentType() != PaymentConstant.PaymentType.ALI_PAY.intValue()) {
            refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()));
        }

        // 还库存 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderItemId", String.valueOf(refundOrder.getOrderItemId()));
        jobParams.put("check", "false");
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_STOCK, jobParams);

        // 同步数据
        refundOrderManager.syncRefundTicketData(refundOrder.getId());
        return new Result(true);
    }

    private MaSendResponse getMaSendResponse(List<MaSendResponse> list, int visitorId, int epMaId) {
        if (list == null) {
            return null;
        }
        for (MaSendResponse response : list) {
            if (response != null && response.getVisitorId() != null && response.getEpMaId() != null
                    && response.getEpMaId() == epMaId && response.getVisitorId() == visitorId) {
                return response;
            }
        }
        return null;
    }
}
