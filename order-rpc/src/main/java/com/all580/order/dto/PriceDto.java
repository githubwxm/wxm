package com.all580.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/7 11:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceDto {
    /** 销售价 */
    private int sale;
    /** 进货价 */
    private int buying;
    /** 门市价 */
    private int shop;
}
