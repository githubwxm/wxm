package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.ConsumeTicketInfo;
import com.all580.order.api.model.ReConsumeTicketInfo;
import com.all580.order.api.model.RefundTicketInfo;
import com.all580.order.api.model.SendTicketInfo;
import com.all580.order.api.service.TicketCallbackService;
import com.all580.order.dao.*;
import com.all580.order.entity.*;
import com.all580.order.manager.RefundOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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
    private MaSendResponseMapper maSendResponseMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private RefundSerialMapper refundSerialMapper;

    @Autowired
    private RefundOrderManager refundOrderManager;

    @Override
    public Result sendTicket(Long orderSn, Integer epMaId, List<SendTicketInfo> infoList) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }
        if (orderItem.getStatus() != OrderConstant.OrderItemStatus.TICKETING) {
            return new Result(false, "订单状态不在出票中");
        }
        orderItem.setStatus(OrderConstant.OrderItemStatus.SEND);
        Date sendMaTime = new Date();
        orderItem.setSendMaTime(sendMaTime);
        orderItemMapper.updateByPrimaryKey(orderItem);

        for (SendTicketInfo ticketInfo : infoList) {
            MaSendResponse response = new MaSendResponse();
            response.setEpMaId(epMaId); // 哪个凭证的
            response.setMaOrderId(ticketInfo.getTicketId()); // 凭证号对应的凭证ID
            response.setSid(ticketInfo.getSid()); // 身份证
            response.setPhone(ticketInfo.getPhone()); // 手机号码
            response.setImageUrl(ticketInfo.getImgUrl()); // 二维码
            response.setVoucherValue(ticketInfo.getVoucherNumber()); // 凭证号
            response.setMaProductId(ticketInfo.getMaProductId()); // 凭证产品ID
            response.setOrderItemId(orderItem.getId()); // 子订单ID
            response.setCreateTime(sendMaTime);
            maSendResponseMapper.insert(response);
        }
        return new Result(true);
    }

    @Override
    public Result consumeTicket(Long orderSn, Integer epMaId, ConsumeTicketInfo info) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        // 获取订单详情 设置核销数量
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        if (detailList == null || detailList.size() < 1) {
            return new Result(false, "订单详情不存在");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        itemDetail.setUsedQuantity(itemDetail.getUsedQuantity() + info.getConsumeQuantity());
        orderItemDetailMapper.updateByPrimaryKey(itemDetail);

        // 保存核销流水
        OrderClearanceSerial serial = new OrderClearanceSerial();
        serial.setOrderItemId(orderItem.getId());
        serial.setClearanceTime(info.getConsumeDate());
        serial.setCreateTime(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(info.getConsumeQuantity());
        serial.setSerialNo(info.getValidateSn());
        orderClearanceSerialMapper.insert(serial);

        // 获取已出票的凭证
        MaSendResponse response = maSendResponseMapper.selectByOrderItemIdAndMaId(orderItem.getId(), info.getTicketId(), epMaId);
        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByMa(itemDetail.getId(), response.getSid(), response.getPhone());
        // 保存核销明细
        OrderClearanceDetail detail = new OrderClearanceDetail();
        detail.setSerialNo(info.getValidateSn());
        detail.setName(visitor.getName());
        detail.setSid(response.getSid());
        detail.setPhone(response.getPhone());
        orderClearanceDetailMapper.insert(detail);

        // 设置核销人核销数量
        visitor.setUseQuantity(visitor.getUseQuantity() + info.getConsumeQuantity());
        visitorMapper.updateByPrimaryKey(visitor);

        // 分账
        /*List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderItem(orderItem.getId());
        Map<Integer, Integer> coreEpIdMap = new HashMap<>();
        if (orderItem.getPaymentFlag() == ProductConstants.PayType.PREPAY) {
            for (OrderItemAccount account : accounts) {
                if (account.getEpId() == account.getCoreEpId().intValue()) {
                    continue;
                }
                if (coreEpId == account.getCoreEpId()) {

                }
            }
        }*/
        return new Result(true);
    }

    @Override
    public Result reConsumeTicket(Long orderSn, Integer epMaId, ReConsumeTicketInfo info) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        // 获取订单详情 设置核销数量
        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        if (detailList == null || detailList.size() < 1) {
            return new Result(false, "订单详情不存在");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        itemDetail.setUsedQuantity(itemDetail.getUsedQuantity() - info.getConsumeQuantity());
        orderItemDetailMapper.updateByPrimaryKey(itemDetail);

        // 保存冲正流水
        ClearanceWashedSerial serial = new ClearanceWashedSerial();
        serial.setSerialNo(info.getReValidateSn());
        serial.setClearanceSerialNo(info.getValidateSn());
        serial.setClearanceWashedTime(info.getReValidateTime());
        serial.setCreateTime(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(info.getConsumeQuantity());

        // 获取已出票的凭证
        MaSendResponse response = maSendResponseMapper.selectByOrderItemIdAndMaId(orderItem.getId(), info.getTicketId(), epMaId);
        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByMa(itemDetail.getId(), response.getSid(), response.getPhone());
        // 设置核销人核销数量
        visitor.setUseQuantity(visitor.getUseQuantity() - info.getConsumeQuantity());
        visitorMapper.updateByPrimaryKey(visitor);

        // 分账
        return new Result(true);
    }

    @Override
    public Result refundTicket(Long orderSn, Integer epMaId, String localSn, String refundSn, Date refundDate, List<RefundTicketInfo> infoList) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
        if (detailList == null || detailList.size() < 1) {
            return new Result(false, "订单详情不存在");
        }
        OrderItemDetail detail = detailList.get(0);

        RefundOrder refundOrder = refundOrderMapper.selectByItemIdAndRefundSn(orderItem.getId(), Long.valueOf(localSn));
        if (refundOrder == null) {
            return new Result(false, "退订订单不存在");
        }

        refundOrder.setStatus(OrderConstant.RefundOrderStatus.REFUND_MONEY); // 退款中
        refundOrder.setRefundTicketTime(refundDate);

        RefundSerial refundSerial = refundSerialMapper.selectByLocalSn(Long.valueOf(localSn));
        if (refundSerial == null) {
            return new Result(false, "退票流水错误");
        }
        refundSerial.setRemoteSerialNo(refundSn);
        refundSerial.setRefundTime(refundDate);

        int quantity = 0;
        for (RefundTicketInfo ticketInfo : infoList) {
            // 获取已出票的凭证
            MaSendResponse response = maSendResponseMapper.selectByOrderItemIdAndMaId(orderItem.getId(), ticketInfo.getTicketId(), epMaId);
            // 获取核销人信息
            Visitor visitor = visitorMapper.selectByMa(detail.getId(), response.getSid(), response.getPhone());
            if (ticketInfo.getRefundQuantity() != visitor.getPreReturn().intValue()) {
                return new Result(false, "退票数据异常,实际退票数与申请退票数不匹配");
            }
            visitor.setReturnQuantity(visitor.getReturnQuantity() + visitor.getPreReturn());
            visitor.setPreReturn(0);
            visitorMapper.updateByPrimaryKey(visitor);
            quantity += ticketInfo.getRefundQuantity();
        }
        orderItem.setRefundQuantity(orderItem.getRefundQuantity() + quantity);
        orderItemMapper.updateByPrimaryKey(orderItem);
        refundOrderMapper.updateByPrimaryKey(refundOrder);
        refundSerialMapper.updateByPrimaryKey(refundSerial);

        // 退款
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrderId());
        refundOrderManager.refundMoney(order, refundOrder.getMoney());
        return new Result(true);
    }
}
