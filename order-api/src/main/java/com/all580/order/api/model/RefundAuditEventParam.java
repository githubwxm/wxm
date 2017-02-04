package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 14:58
 */
@Data
@AllArgsConstructor
public class RefundAuditEventParam implements Serializable {
    private static final long serialVersionUID = 4241014262026210321L;
    private Integer refundId;

    private boolean status;
}
