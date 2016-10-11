package com.all580.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/8 19:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateAccountDto {
    private int subtractEpId;

    private int subtractCoreId;

    private int orderItemId;

    private int addEpId;

    private int addCoreId;

    private int money;

    private int subtractProfit;

    private int addProfit;

    private List<AccountDataDto> subtractData;

    private List<AccountDataDto> addData;
}
