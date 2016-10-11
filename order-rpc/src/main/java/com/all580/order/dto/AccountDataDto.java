package com.all580.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @JsonIgnore
    private Integer saleCoreEpId;
}
