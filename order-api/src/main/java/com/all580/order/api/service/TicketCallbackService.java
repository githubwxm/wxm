package com.all580.order.api.service;

import com.all580.order.api.model.ConsumeTicketInfo;
import com.all580.order.api.model.ReConsumeTicketInfo;
import com.all580.order.api.model.RefundTicketInfo;
import com.all580.order.api.model.SendTicketInfo;
import com.framework.common.Result;

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
     * @param epMaId 凭证ID
     * @param infoList 票信息
     * @return
     */
    Result sendTicket(Long orderSn, Integer epMaId, List<SendTicketInfo> infoList);

    /**
     * 消费验票通知
     * @param info 验票信息
     * @return
     */
    Result consumeTicket(Long orderSn, Integer epMaId, ConsumeTicketInfo info);

    /**
     * 反核销通知
     * @param info 反核销信息
     * @return
     */
    Result reConsumeTicket(Long orderSn, Integer epMaId, ReConsumeTicketInfo info);

    /**
     * 退票回调
     * @param info 退票信息
     * @return
     */
    Result refundTicket(Long orderSn, Integer epMaId, RefundTicketInfo info);
}
