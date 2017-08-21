package com.all580.order.dto;

import com.all580.order.entity.Order;
import lombok.Data;

import java.util.List;

/**
 * Created by xiangzw on 2017/8/17.
 */
@Data
public class PackageOrderDto extends Order{
    //上层的套票订单
    //private Order packageOrder;
    //下层的元素订单
    private List<Order> packageItemOrders;
}
