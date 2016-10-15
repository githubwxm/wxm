package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 核销验票信息 消费
 * @date 2016/10/15 9:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeTicketInfo {
    /** 验票流水 */
    private String validateSn;

    /** 电子凭证号 */
    private String voucherNumber;

    /** 票ID */
    private String ticketId;

    /** 消费张数 */
    private Integer consumeQuantity;

    /** 消费时间 */
    private Date consumeDate;
}
