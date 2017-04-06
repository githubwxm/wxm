package com.all580.order.service;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.model.*;
import com.all580.order.api.service.TicketCallbackService;
import com.all580.order.dao.*;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.LockTransactionManager;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventAspect;
import com.framework.common.lang.JsonUtils;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.*;

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
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private GroupConsumeMapper groupConsumeMapper;

    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Autowired
    private LockTransactionManager lockTransactionManager;
    @Autowired
    private MnsEventAspect eventManager;
    @Autowired
    private JobAspect jobManager;

    @Override
    @MnsEvent
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

        // 触发事件
        eventManager.addEvent(OrderConstant.EventType.SEND_TICKET, orderItem.getId());
        return new Result(true);
    }

    @Override
    @MnsEvent
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

        // 触发事件
        eventManager.addEvent(OrderConstant.EventType.SEND_TICKET, orderItem.getId());
        return new Result(true);
    }

    @Override
    @MnsEvent
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            return new Result(false, "订单不存在");
        }


        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        int ret = orderItemDetailMapper.useQuantity(itemDetail.getId(), info.getConsumeQuantity());
        if (ret <= 0) {
            log.warn("核销流水: {} 订单:{} 核销票不足", info.getValidateSn(), orderSn);
            throw new ApiException("没有可核销的票");
        }

        // 保存核销流水
        OrderClearanceSerial serial = bookingOrderManager.saveClearanceSerial(orderItem, order.getPayee_ep_id(), itemDetail.getDay(), info.getConsumeQuantity(), info.getValidateSn(), procTime);

        // 获取核销人信息
        Visitor visitor;
        if (info.getVisitorSeqId() == null) {
            List<Visitor> visitors = visitorMapper.selectByOrderItem(orderItem.getId());
            if (visitors.size() > 1) {
                throw new ApiException("该订单有多个游客");
            }
            visitor = visitors.get(0);
        } else {
            visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        }
        // 保存核销明细
        OrderClearanceDetail detail = new OrderClearanceDetail();
        detail.setSerial_no(info.getValidateSn());
        detail.setName(visitor.getName());
        detail.setSid(visitor.getSid());
        detail.setPhone(visitor.getPhone());
        orderClearanceDetailMapper.insertSelective(detail);

        // 设置核销人核销数量
        visitorMapper.useQuantity(visitor.getId(), info.getConsumeQuantity());
        // 修改已用数量
        orderItemMapper.useQuantity(orderItem.getId(), info.getConsumeQuantity());

        eventManager.addEvent(OrderConstant.EventType.CONSUME_TICKET, new ConsumeTicketEventParam(orderItem.getId(), serial.getId()));
        return new Result(true);
    }

    @Override
    @MnsEvent
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

        if (!(orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            return new Result(false, "该订单不是团队订单");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        int ret = orderItemDetailMapper.useQuantity(itemDetail.getId(), info.getConsumeQuantity());
        if (ret <= 0) {
            log.warn("核销流水: {} 订单:{} 核销票不足", info.getValidateSn(), orderSn);
            throw new ApiException("没有可核销的票");
        }

        // 保存核销流水
        OrderClearanceSerial serial = bookingOrderManager.saveClearanceSerial(orderItem, order.getPayee_ep_id(), itemDetail.getDay(), info.getConsumeQuantity(), info.getValidateSn(), procTime);

        // 保存核销明细
        List<String> sids = info.getSids();
        if (sids != null) {
            for (String sid : sids) {
                GroupConsume groupConsume = new GroupConsume();
                groupConsume.setGroup_id(orderItem.getGroup_id());
                groupConsume.setOrder_item_id(orderItem.getId());
                groupConsume.setClearance_serial_id(serial.getId());
                groupConsume.setSid(sid);
                groupConsume.setCreate_time(new Date());
                groupConsumeMapper.insertSelective(groupConsume);
            }
        }

        // 修改已用数量
        orderItemMapper.useQuantity(orderItem.getId(), info.getConsumeQuantity());

        eventManager.addEvent(OrderConstant.EventType.CONSUME_TICKET, new ConsumeTicketEventParam(orderItem.getId(), serial.getId()));
        return new Result(true);
    }

    @Override
    @JobTask
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        orderItemDetailMapper.useQuantity(itemDetail.getId(), -orderClearanceSerial.getQuantity());

        // 保存冲正流水
        ClearanceWashedSerial serial = saveWashedSerial(orderItem, order.getPayee_ep_id(),
                orderClearanceSerial.getQuantity(), info.getReValidateSn(), info.getValidateSn(), procTime);

        // 获取核销人信息
        Visitor visitor = visitorMapper.selectByPrimaryKey(info.getVisitorSeqId());
        // 设置核销人核销数量
        visitorMapper.useQuantity(visitor.getId(), -orderClearanceSerial.getQuantity());
        // 修改已用数量
        orderItemMapper.useQuantity(orderItem.getId(), -orderClearanceSerial.getQuantity());

        // 发送短信
        //smsManager.sendReConsumeSms(orderItem, orderClearanceSerial.getQuantity(), orderClearanceSerial.getQuantity());

        // 分账
        // 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerial_no());
        jobManager.addJob(OrderConstant.Actions.RE_CONSUME_SPLIT_ACCOUNT, Collections.singleton(jobParams));

        // 同步数据
        bookingOrderManager.syncReConsumeData(orderItem.getId(), info.getReValidateSn());
        return new Result(true);
    }

    @Override
    @JobTask
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
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

        if (orderItem.getGroup_id() == null || orderItem.getGroup_id() == 0) {
            return new Result(false, "该订单不是团队订单");
        }

        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            return new Result(false, "订单不存在");
        }

        ClearanceWashedSerial oldSerial = clearanceWashedSerialMapper.selectBySn(info.getReValidateSn());
        if (oldSerial != null) {
            return new Result(false, "反核销流水:" + info.getReValidateSn() + "重复冲正");
        }

        // 目前景点只有一天
        OrderItemDetail itemDetail = detailList.get(0);
        orderItemDetailMapper.useQuantity(itemDetail.getId(), -info.getQuantity());

        // 保存冲正流水
        ClearanceWashedSerial serial = saveWashedSerial(orderItem, order.getPayee_ep_id(),
                orderClearanceSerial.getQuantity(), info.getReValidateSn(), info.getValidateSn(), procTime);

        // 修改已用数量
        orderItemMapper.useQuantity(orderItem.getId(), -info.getQuantity());

        // 发送短信
        //smsManager.sendReConsumeSms(orderItem, info.getQuantity(), orderClearanceSerial.getQuantity());

        // 分账
        // 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("sn", serial.getSerial_no());
        jobManager.addJob(OrderConstant.Actions.RE_CONSUME_SPLIT_ACCOUNT, Collections.singleton(jobParams));

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

        RefundOrder refundOrder = refundOrderMapper.selectByItemIdAndRefundSn(orderItem.getId(), Long.valueOf(info.getRefId()));
        if (refundOrder == null) {
            return new Result(false, "退订订单不存在");
        }

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(info.getRefId(), lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.refundTicket(info, procTime, orderItem, refundOrder);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result refundTicket(Long orderSn, boolean success, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        Assert.notNull(orderItem, "订单不存在");
        List<RefundOrder> refundOrders = refundOrderMapper.selectByItemId(orderItem.getId());
        Assert.notEmpty(refundOrders, "该订单没有申请退订");
        RefundOrder refundOrderResult = null;
        for (RefundOrder refundOrder : refundOrders) {
            if (refundOrder.getStatus() == OrderConstant.RefundOrderStatus.REFUNDING) {
                if (refundOrderResult != null) {
                    throw new ApiException("该订单已申请多个退订");
                }
                refundOrderResult = refundOrder;
            }
        }
        if (refundOrderResult == null) {
            throw new ApiException(Result.UNIQUE_KEY_ERROR, "没有在退订中的退订订单");
        }
        List<Visitor> visitors = visitorMapper.selectByOrderItem(orderItem.getId());
        if (visitors.size() > 1) {
            throw new ApiException("该订单有多个游客");
        }
        String refId = String.valueOf(refundOrderResult.getLocal_refund_serial_no());
        RefundTicketInfo info = new RefundTicketInfo(visitors.get(0).getId(), refId, success);
        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(refId, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.refundTicket(info, procTime, orderItem, refundOrderResult);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result refundGroupTicket(Long orderSn, RefundGroupTicketInfo info, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }

        RefundOrder refundOrder = refundOrderMapper.selectByItemIdAndRefundSn(orderItem.getId(), Long.valueOf(info.getRefId()));
        if (refundOrder == null) {
            return new Result(false, "退订订单不存在");
        }

        if (orderItem.getGroup_id() == null || orderItem.getGroup_id() == 0) {
            return new Result(false, "该订单不是团队订单");
        }

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(info.getRefId(), lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.refundGroupTicket(info, procTime, orderItem, refundOrder);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result modifyGroupTicket(Long orderSn, boolean success, Date procTime) {
        OrderItem orderItem = orderItemMapper.selectBySN(orderSn);
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }
        if (orderItem.getGroup_id() == null || orderItem.getGroup_id() == 0) {
            return new Result(false, "该订单不是团队订单");
        }
        if (orderItem.getStatus() != OrderConstant.OrderItemStatus.MODIFYING) {
            return new Result(false, "该订单不在修改中状态");
        }

        orderItem.setStatus(success ? OrderConstant.OrderItemStatus.MODIFY : OrderConstant.OrderItemStatus.MODIFY_FAIL);
        orderItemMapper.updateByPrimaryKeySelective(orderItem);

        // 同步数据
        bookingOrderManager.syncSendingData(orderItem.getId());
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

    private ClearanceWashedSerial saveWashedSerial(OrderItem orderItem, int saleCoreEpId, int quantity, String sn, String clearanceSn, Date procTime) {
        ClearanceWashedSerial serial = new ClearanceWashedSerial();
        serial.setSerial_no(sn);
        serial.setClearance_serial_no(clearanceSn);
        serial.setClearance_washed_time(procTime);
        serial.setCreate_time(new Date());
        serial.setDay(orderItem.getStart());
        serial.setQuantity(quantity);
        // 获取本次核销 供应平台商应得金额
        int money = bookingOrderManager.getOutPriceForEp(orderItem.getId(), orderItem.getSupplier_core_ep_id(),
                orderItem.getSupplier_core_ep_id(), serial.getDay(), serial.getQuantity());
        serial.setSupplier_money(money);
        // 获取平台商通道费率
        serial.setChannel_fee(bookingOrderManager.getChannelRate(orderItem.getSupplier_core_ep_id(), saleCoreEpId));
        clearanceWashedSerialMapper.insertSelective(serial);
        return serial;
    }
}
