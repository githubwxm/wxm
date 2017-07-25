package com.all580.order.dao;

import com.all580.order.entity.RefundPackageOrder;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xiangzw on 2017/7/24.
 */
public interface RefundPackageOrderMapper {

    int insertSelective(RefundPackageOrder record);

    int updateByPrimaryKeySelective(RefundPackageOrder record);

    RefundPackageOrder selectByItemIdAndOuter(@Param("itemId") Integer itemId, @Param("outer") String outer);
}
