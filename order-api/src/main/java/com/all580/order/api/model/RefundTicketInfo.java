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
    /** 票ID */
    private String ticketId;

    /** 电子凭证号 */
    private String voucherNumber;

    /** 退票张数 */
    private Integer refundQuantity;
}
