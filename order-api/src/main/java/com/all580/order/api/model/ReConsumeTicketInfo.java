package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 反核销回调信息
 * @date 2016/10/15 9:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReConsumeTicketInfo {
    /** 验票流水 */
    private String validateSn;

    /** 反核销流水 */
    private String reValidateSn;

    /** 电子凭证号 */
    private String voucherNumber;

    /** 票ID */
    private String ticketId;

    /** 消费张数 */
    private Integer consumeQuantity;

    /** 反核销时间 */
    private Date reValidateTime;
}
