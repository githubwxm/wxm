package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 核销验票信息 消费
 * @date 2016/10/15 9:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeGroupTicketInfo implements Serializable {
    private static final long serialVersionUID = 8850195798766948157L;
    /** 验票流水 */
    private String validateSn;

    /** 证件集合 */
    private List<String> sids;

    /** 电子凭证号 */
    private String voucherNumber;

    /** 票ID */
    private String ticketId;

    /** 消费张数 */
    private Integer consumeQuantity;
}
