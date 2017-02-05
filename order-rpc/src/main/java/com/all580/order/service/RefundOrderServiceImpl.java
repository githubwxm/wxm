package com.all580.order.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.RefundOrderService;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.LockTransactionManager;
import com.all580.order.manager.RefundOrderManager;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退订服务
 * @date 2016/10/11 10:49
 */
@Service
@Slf4j
public class RefundOrderServiceImpl implements RefundOrderService {
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Autowired
    private LockTransactionManager lockTransactionManager;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> cancel(Map params) {
        if (!params.containsKey(EpConstant.EpKey.EP_ID)) {
            throw new ApiException("非法请求:企业ID为空");
        }
        Order order = orderMapper.selectBySN(Long.valueOf(params.get("order_sn").toString()));
        if (!String.valueOf(params.get(EpConstant.EpKey.EP_ID)).equals(String.valueOf(order.getBuy_ep_id()))
                && !String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(order.getPayee_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能取消该订单");
        }
        return refundOrderManager.cancel(order);
    }

    @Override
    public Result<?> apply(Map params) throws Exception {
        String orderItemSn = params.get("order_item_sn").toString();
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(orderItemSn));
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        // 供应侧/销售侧
        Integer applyFrom = CommonUtil.objectParseInteger(params.get("apply_from"));

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.applyRefund(params, orderItem, order, applyFrom);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> applyForGroup(Map params) throws Exception {
        String orderItemSn = params.get("order_item_sn").toString();
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(orderItemSn));
        if (orderItem == null) {
            throw new ApiException("订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }

        if (!(orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            throw new ApiException("该订单不是团队订单");
        }

        // 供应侧/销售侧
        Integer applyFrom = CommonUtil.objectParseInteger(params.get("apply_from"));

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.applyRefundForGroup(params, orderItem, order, applyFrom);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> cancelNoSplit(Map params) {
        String orderSn = params.get("order_sn").toString();
        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.cancelNoSplit(orderSn);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> audit(Map params) throws Exception {
        // 分布式锁
        String refundSn = params.get("refund_sn").toString();
        DistributedReentrantLock lock = distributedLockTemplate.execute(refundSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.auditRefund(params, refundSn);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> refundAliPayMoney(Map params) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(params.get("refund_sn").toString()));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.REFUND_MONEY) {
            throw new ApiException("退订订单不在退款中状态");
        }
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(order.getPayee_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能退款该退订订单");
        }
        // 退款
        Result result = refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder.getId());
        if (result == null) {
            throw new ApiException("调用退款返回null");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> refundMoneyAudit(Map params) {
        RefundOrder refundOrder = refundOrderMapper.selectBySN(Long.valueOf(params.get("refund_sn").toString()));
        if (refundOrder == null) {
            throw new ApiException("退订订单不存在");
        }
        if (refundOrder.getStatus() != OrderConstant.RefundOrderStatus.REFUND_MONEY_AUDITING) {
            throw new ApiException("退订订单不在退款待审核状态");
        }
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(refundOrder.getOrder_item_id());
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!String.valueOf(params.get(EpConstant.EpKey.CORE_EP_ID)).equals(String.valueOf(order.getPayee_ep_id()))) {
            throw new ApiException("非法请求:当前企业不能审核退款该退订订单");
        }
        refundOrder.setAudit_money_time(new Date());
        refundOrder.setAudit_money_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        refundOrder.setAudit_money_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        // 退款
        Result result = refundOrderManager.refundMoney(order, refundOrder.getMoney(), String.valueOf(refundOrder.getNumber()), refundOrder);
        if (result == null) {
            throw new ApiException("调用退款返回null");
        }
        return result;
    }
}
