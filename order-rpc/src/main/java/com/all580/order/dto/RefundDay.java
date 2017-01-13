package com.all580.order.dto;

import com.all580.order.entity.Visitor;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退票信息
 * @date 2017/1/13 10:34
 */
@Data
public class RefundDay {
    private Date day;

    private int quantity;

    private List<Visitor> visitors;

    private int fee;
}
