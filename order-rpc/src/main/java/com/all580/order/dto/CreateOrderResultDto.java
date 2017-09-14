package com.all580.order.dto;

import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.framework.common.Result;
import lombok.Data;

import java.util.List;

/**
 * Created by xiangzw on 2017/8/16.
 */
@Data
public class CreateOrderResultDto {
    //创建的订单
    private Order order;
    //创建的子订单
    private List<OrderItem> orderItems;
    //是否需要创建套票元素订单
    private boolean hasPackageOrderItem;
    //需要创建套票订单的子订单项
    private List<OrderItem> packageOrderItems;
    //创建套票元素订单时的结果集
    private List<CreateOrderResultDto> PackageCreateOrders;

    private boolean uniqueKey = Boolean.TRUE;
    private Result<?> result;
}
