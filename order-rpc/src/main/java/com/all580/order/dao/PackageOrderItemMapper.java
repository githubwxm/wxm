package com.all580.order.dao;

import com.all580.order.entity.PackageOrderItem;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xiangzw on 2017/7/13.
 */
public interface PackageOrderItemMapper {

    int insertSelective(PackageOrderItem record);

    int updateByPrimaryKeySelective(PackageOrderItem record);

    PackageOrderItem selectByNumber(@Param("number") Long number);
}
