package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退票回调信息
 * @date 2016/10/15 9:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundTicketInfo {
    /** 游客ID */
    private int visitorSeqId;

    /** 退票流水 */
    private String refId;

    /** 退票是否处理成功 */
    private boolean success;
}
