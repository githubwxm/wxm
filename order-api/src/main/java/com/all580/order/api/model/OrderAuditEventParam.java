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
public class OrderAuditEventParam implements Serializable {
    private static final long serialVersionUID = -5786316284524435333L;

    private Integer itemId;

    private boolean status;
}
