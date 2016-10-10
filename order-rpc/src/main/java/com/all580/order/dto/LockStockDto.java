package com.all580.order.dto;

import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private OrderItem orderItem;

    private List<OrderItemDetail> orderItemDetail;
}
