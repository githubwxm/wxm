package com.all580.order.dto;

import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.PackageOrderItem;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/8 10:17
 */
@Data
public class RefundOrderApply {
    private long itemNo;

    private OrderItem item;

    private Order order;

    private int from;

    private int epId;

    private Integer quantity;

    private String cause;

    private Date date;

    private Integer userId;

    private String userName;

    /**套票元素子订单*/
    private List<OrderItem> orderItems;

    private PackageOrderItem packageOrderItem;

    /** 外部信息 */
    private String outer;
}
