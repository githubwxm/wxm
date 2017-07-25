package com.all580.order.dao;

import com.all580.order.entity.PackageOrderItemSalesChain;

/**
 * Created by xiangzw on 2017/7/24.
 */
public interface PackageOrderItemSalesChainMapper {

    int insertSelective(PackageOrderItemSalesChain record);

    int updateByPrimaryKeySelective(PackageOrderItemSalesChain record);
}
