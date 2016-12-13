package com.all580.order.api.service;

import com.all580.order.api.model.*;
import com.framework.common.Result;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 票务凭证回调服务
 * @date 2016/10/15 9:18
 */
public interface TicketCallbackService {
    /**
     * 出票回调
     * @param orderSn 子订单流水编号
     * @param infoList 票信息
     * @param procTime 处理时间
     * @return
     */
    Result sendTicket(Long orderSn, List<SendTicketInfo> infoList, Date procTime);

    /**
     * 出票回调
     * @param orderSn 子订单流水编号
     * @param info 票信息
     * @param procTime 处理时间
     * @return
     */
    Result sendGroupTicket(Long orderSn,SendTicketInfo info, Date procTime);

    /**
     * 消费验票通知
     * @param orderSn 子订单流水编号
     * @param info 验票信息
     * @param procTime 处理时间
     * @return
     */
    Result consumeTicket(Long orderSn, ConsumeTicketInfo info, Date procTime);

    /**
     * 消费验票通知
     * @param orderSn 子订单流水编号
     * @param info 验票信息
     * @param procTime 处理时间
     * @return
     */
    Result consumeGroupTicket(Long orderSn, ConsumeGroupTicketInfo info, Date procTime);

    /**
     * 反核销通知
     * @param orderSn 子订单流水编号
     * @param info 反核销信息
     * @param procTime 处理时间
     * @return
     */
    Result reConsumeTicket(Long orderSn, ReConsumeTicketInfo info, Date procTime);

    /**
     * 反核销通知
     * @param orderSn 子订单流水编号
     * @param info 反核销信息
     * @param procTime 处理时间
     * @return
     */
    Result reConsumeGroupTicket(Long orderSn, ReConsumeGroupTicketInfo info, Date procTime);

    /**
     * 退票回调
     * @param orderSn 子订单流水编号
     * @param info 退票信息
     * @param procTime 处理时间
     * @return
     */
    Result refundTicket(Long orderSn, RefundTicketInfo info, Date procTime);

    /**
     * 退票回调
     * @param orderSn 子订单流水编号
     * @param info 退票信息
     * @param procTime 处理时间
     * @return
     */
    Result refundGroupTicket(Long orderSn, RefundGroupTicketInfo info, Date procTime);

    /**
     * 修改回调
     * @param orderSn 子订单流水编号
     * @param success 成功与否
     * @return
     */
    Result modifyGroupTicket(Long orderSn, boolean success, Date procTime);
}
