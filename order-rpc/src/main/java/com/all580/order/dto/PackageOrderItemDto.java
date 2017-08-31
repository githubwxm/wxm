package com.all580.order.dto;

import com.all580.order.entity.OrderItem;
import lombok.Data;

import java.util.List;

/**
 * Created by xiangzw on 2017/8/18.
 */
@Data
public class PackageOrderItemDto extends OrderItem {
    //下层的元素子订单
    private List<OrderItem> packageOrderItems;
}
