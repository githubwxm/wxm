package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.*;
import com.all580.order.api.service.TicketCallbackService;
import com.all580.order.dao.*;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
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
    private GroupConsumeMapper groupConsumeMapper;

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
        orderItem.setSend_ma_time(procTime);
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        List<MaSendResponse> maSendResponseList = maSendResponseMapper.selectByOrderItemId(orderItem.getId());
        for (SendTicketInfo ticketInfo : infoList) {
            // check
            if (getMaSendResponse(maSendResponseList, ticketInfo.getVisitorSeqId(), orderItem.getEp_ma_id()) != null) {
                continue;
            }
            MaSendResponse response = new MaSendResponse();
            response.setVisitor_id(ticketInfo.getVisitorSeqId()); // 游客ID
            response.setEp_ma_id(orderItem.getEp_ma_id()); // 哪个凭证的商户ID
            response.setMa_order_id(ticketInfo.getTicketId()); // 凭证号对应的凭证ID
            response.setSid(ticketInfo.getSid()); // 身份证
            response.setPhone(ticketInfo.getPhone()); // 手机号码
            response.setImage_url(ticketInfo.getImgUrl()); // 二维码
            response.setVoucher_value(ticketInfo.getVoucherNumber()); // 凭证号
            response.setMa_product_id(ticketInfo.getMaProductId()); // 凭证产品ID
            response.setOrder_item_id(orderItem.getId()); // 子订单ID
            response.setCreate_time(procTime);
            maSendResponseMapper.insertSelective(response);
        }

        // 同步数据
        bookingOrderManager.syncSendTicketData(orderItem.getId());
        return new Result(true);
    }

    @Override
    public Result sendGroupTicket(Long orderSn, SendTicketInfo info, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }
        if (orderItem.getStatus() != OrderConstant.OrderItemStatus.TICKETING) {
            return new Result(false, "订单状态不在出票中");
        }
        if (orderItem.getGroup_id() == null || orderItem.getGroup_id() == 0) {
            return new Result(false, "该订单不是团队订单");
        }
        orderItem.setStatus(OrderConstant.OrderItemStatus.SEND);
        orderItem.setSend_ma_time(procTime);
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        List<MaSendResponse> maSendResponseList = maSendResponseMapper.selectByOrderItemId(orderItem.getId());
        // check
        if (getMaSendResponse(maSendResponseList, 0, orderItem.getEp_ma_id()) != null) {
            log.warn("团队订单号:{} 重复出票:{}", orderSn, JsonUtils.toJson(info));
            return new Result(true);
        }
        MaSendResponse response = new MaSendResponse();
        response.setVisitor_id(0); // 游客ID 团队 0
        response.setEp_ma_id(orderItem.getEp_ma_id()); // 哪个凭证的商户ID
        response.setMa_order_id(info.getTicketId()); // 凭证号对应的凭证ID
        response.setSid(info.getSid()); // 身份证
        response.setPhone(info.getPhone()); // 手机号码
        response.setImage_url(info.getImgUrl()); // 二维码
        response.setVoucher_value(info.getVoucherNumber()); // 凭证号
        response.setMa_product_id(info.getMaProductId()); // 凭证产品ID
        response.setOrder_item_id(orderItem.getId()); // 子订单ID
        response.setCreate_time(procTime);
        maSendResponseMapper.insertSelective(response);

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
        itemDetail.setUsed_quantity(itemDetail.getUsed_quantity() + info.getConsumeQuantity());
        orderItemDetailMapper.updateByPrimaryKeySelective(itemDetail);

        // 保存核销流水
        OrderClearanceSerial serial = new OrderClearanceSerial();
        serial.setOrder_item_id(orderItem.getId());
        serial.setClearance_time(procTime);
        serial.setCreate_time(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(info.getConsumeQuantity());
        serial.setSerial_no(info.getValidateSn());
        orderClearanceSerialMapper.insertSelective(serial);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        // 保存核销明细
        OrderClearanceDetail detail = new OrderClearanceDetail();
        detail.setSerial_no(info.getValidateSn());
        detail.setName(visitor.getName());
        detail.setSid(visitor.getSid());
        detail.setPhone(visitor.getPhone());
        orderClearanceDetailMapper.insertSelective(detail);

        // 设置核销人核销数量
        visitor.setUse_quantity(visitor.getUse_quantity() + info.getConsumeQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);
        // 修改已用数量
        orderItem.setUsed_quantity(orderItem.getUsed_quantity() + info.getConsumeQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 发送短信
        smsManager.sendConsumeSms(orderItem, info.getConsumeQuantity());

        // 分账
        // 核销成功 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerial_no());
        refundOrderManager.addJob(OrderConstant.Actions.CONSUME_SPLIT_ACCOUNT, jobParams);

        // 同步数据
        bookingOrderManager.syncConsumeData(orderItem.getId(), info.getValidateSn());
        return new Result(true);
    }

    @Override
    public Result consumeGroupTicket(Long orderSn, ConsumeGroupTicketInfo info, Date procTime) {
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

        if (orderItem.getGroup_id() == null || orderItem.getGroup_id() == 0) {
            return new Result(false, "该订单不是团队订单");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        itemDetail.setUsed_quantity(itemDetail.getUsed_quantity() + info.getConsumeQuantity());
        orderItemDetailMapper.updateByPrimaryKeySelective(itemDetail);

        // 保存核销流水
        OrderClearanceSerial serial = new OrderClearanceSerial();
        serial.setOrder_item_id(orderItem.getId());
        serial.setClearance_time(procTime);
        serial.setCreate_time(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(info.getConsumeQuantity());
        serial.setSerial_no(info.getValidateSn());
        orderClearanceSerialMapper.insertSelective(serial);

        // 保存核销明细
        List<String> sids = info.getSids();
        if (sids != null) {
            for (String sid : sids) {
                GroupConsume groupConsume = new GroupConsume();
                groupConsume.setGroup_id(orderItem.getGroup_id());
                groupConsume.setOrder_item_id(orderItem.getId());
                groupConsume.setClearance_serial_id(serial.getId());
                groupConsume.setSid(sid);
                groupConsumeMapper.insertSelective(groupConsume);
            }
        }

        // 修改已用数量
        orderItem.setUsed_quantity(orderItem.getUsed_quantity() + info.getConsumeQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 发送短信
        smsManager.sendConsumeSms(orderItem, info.getConsumeQuantity());

        // 分账
        // 核销成功 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerial_no());
        refundOrderManager.addJob(OrderConstant.Actions.CONSUME_SPLIT_ACCOUNT, jobParams);

        // 同步数据
        bookingOrderManager.syncConsumeDataForGroup(orderItem.getId(), info.getValidateSn());
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
        itemDetail.setUsed_quantity(itemDetail.getUsed_quantity() - orderClearanceSerial.getQuantity());
        orderItemDetailMapper.updateByPrimaryKeySelective(itemDetail);

        // 保存冲正流水
        ClearanceWashedSerial serial = new ClearanceWashedSerial();
        serial.setSerial_no(info.getReValidateSn());
        serial.setClearance_serial_no(info.getValidateSn());
        serial.setClearance_washed_time(procTime);
        serial.setCreate_time(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(orderClearanceSerial.getQuantity());
        clearanceWashedSerialMapper.insertSelective(serial);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        // 设置核销人核销数量
        visitor.setUse_quantity(visitor.getUse_quantity() - orderClearanceSerial.getQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);
        // 修改已用数量
        orderItem.setUsed_quantity(orderItem.getUsed_quantity() - orderClearanceSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 发送短信
        smsManager.sendReConsumeSms(orderItem, orderClearanceSerial.getQuantity(), orderClearanceSerial.getQuantity());

        // 分账
        // 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerial_no());
        refundOrderManager.addJob(OrderConstant.Actions.RE_CONSUME_SPLIT_ACCOUNT, jobParams);

        // 同步数据
        bookingOrderManager.syncReConsumeData(orderItem.getId(), info.getReValidateSn());
        return new Result(true);
    }

    @Override
    public Result reConsumeGroupTicket(Long orderSn, ReConsumeGroupTicketInfo info, Date procTime) {
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

        if (orderItem.getGroup_id() == null || orderItem.getGroup_id() == 0) {
            return new Result(false, "该订单不是团队订单");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        itemDetail.setUsed_quantity(itemDetail.getUsed_quantity() - info.getQuantity());
        orderItemDetailMapper.updateByPrimaryKeySelective(itemDetail);

        // 保存冲正流水
        ClearanceWashedSerial serial = new ClearanceWashedSerial();
        serial.setSerial_no(info.getReValidateSn());
        serial.setClearance_serial_no(info.getValidateSn());
        serial.setClearance_washed_time(procTime);
        serial.setCreate_time(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(info.getQuantity());
        clearanceWashedSerialMapper.insertSelective(serial);

        // 修改已用数量
        orderItem.setUsed_quantity(orderItem.getUsed_quantity() - info.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 发送短信
        smsManager.sendReConsumeSms(orderItem, info.getQuantity(), orderClearanceSerial.getQuantity());

        // 分账
        // 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerial_no());
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

        if (refundSerial.getRefund_time() != null) {
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

        refundOrder.setRefund_ticket_time(procTime);

        //refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefund_time(procTime);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        visitor.setReturn_quantity(visitor.getReturn_quantity() + refundSerial.getQuantity());
        visitorMapper.updateByPrimaryKeySelective(visitor);

        RefundVisitor refundVisitor = refundVisitorMapper.selectByRefundIdAndVisitorId(refundOrder.getId(), info.getVisitorSeqId());
        refundVisitor.setReturn_quantity(refundVisitor.getReturn_quantity() + refundSerial.getQuantity());
        refundVisitor.setPre_quantity(0);
        refundVisitorMapper.updateByPrimaryKeySelective(refundVisitor);
        orderItem.setRefund_quantity(orderItem.getRefund_quantity() + refundSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        refundSerialMapper.updateByPrimaryKeySelective(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), orderItem, refundOrder);

        // 还库存 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderItemId", String.valueOf(refundOrder.getOrder_item_id()));
        jobParams.put("check", "false");
        bookingOrderManager.addJob(OrderConstant.Actions.REFUND_STOCK, jobParams);

        // 同步数据
        refundOrderManager.syncRefundTicketData(refundOrder.getId());
        return new Result(true);
    }

    @Override
    public Result refundGroupTicket(Long orderSn, RefundGroupTicketInfo info, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        RefundSerial refundSerial = refundSerialMapper.selectByLocalSn(Long.valueOf(info.getRefId()));
        if (refundSerial == null) {
            return new Result(false, "退票流水错误");
        }

        if (refundSerial.getRefund_time() != null) {
            return new Result(false, "退票流水:" + info.getRefId() + "重复操作");
        }

        RefundOrder refundOrder = refundOrderMapper.selectByItemIdAndRefundSn(orderItem.getId(), Long.valueOf(info.getRefId()));
        if (refundOrder == null) {
            return new Result(false, "退订订单不存在");
        }

        if (orderItem.getGroup_id() == null || orderItem.getGroup_id() == 0) {
            return new Result(false, "该订单不是团队订单");
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

        refundOrder.setRefund_ticket_time(procTime);

        //refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefund_time(procTime);

        orderItem.setRefund_quantity(orderItem.getRefund_quantity() + refundSerial.getQuantity());
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
        refundSerialMapper.updateByPrimaryKeySelective(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), orderItem, refundOrder);

        // 还库存 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderItemId", String.valueOf(refundOrder.getOrder_item_id()));
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
            if (response != null && response.getVisitor_id() != null && response.getEp_ma_id() != null
                    && response.getEp_ma_id() == epMaId && response.getVisitor_id() == visitorId) {
                return response;
            }
        }
        return null;
    }
}
