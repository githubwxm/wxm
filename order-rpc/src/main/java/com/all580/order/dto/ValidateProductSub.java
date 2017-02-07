package com.all580.order.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/7 11:02
 */
@Data
public class ValidateProductSub {
    private long code;
    private int quantity;
    private int days;
    private Date booking;
    /** 团队ID */
    private Integer groupId;
    private String memo;

    public int getAllQuantity() {
        return quantity * days;
    }
}
