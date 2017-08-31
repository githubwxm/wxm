package com.all580.order.dto;

import com.all580.order.entity.RefundOrder;
import lombok.Data;

import java.util.List;

/**
 * Created by xiangzw on 2017/8/21.
 */
@Data
public class PackageRefundOrderDto extends RefundOrder {
    //下层元素订单的所有子订单
    List<RefundOrder> refundOrderItems;
}
