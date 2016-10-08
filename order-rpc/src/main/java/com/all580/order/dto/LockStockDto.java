package com.all580.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/8 15:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockStockDto {
    private Integer orderItemId;

    private Integer productSubId;

    private Date bookingDate;

    private Integer days;

    private Integer quantity;
}
