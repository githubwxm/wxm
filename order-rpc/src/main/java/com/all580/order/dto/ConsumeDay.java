package com.all580.order.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 核销信息
 * @date 2017/1/13 10:34
 */
@Data
public class ConsumeDay {
    private Date day;

    private int quantity;
}
