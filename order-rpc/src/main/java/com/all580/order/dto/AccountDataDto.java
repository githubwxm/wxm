package com.all580.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/9 17:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDataDto {
    private int outPrice;

    private int inPrice;

    private int profit;

    private Date day;

    private int saleEpId;

    private int coreEpId;

    private Integer saleCoreEpId;
}
