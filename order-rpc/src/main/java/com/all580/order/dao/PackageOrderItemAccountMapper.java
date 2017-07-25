package com.all580.order.dao;

import com.all580.order.entity.PackageOrderItemAccount;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xiangzw on 2017/7/24.
 */
public interface PackageOrderItemAccountMapper {

    int insertSelective(PackageOrderItemAccount record);

    int updateByPrimaryKeySelective(PackageOrderItemAccount record);

    PackageOrderItemAccount selectByOrderItemAndEp(@Param("itemId") Integer itemId, @Param("epId") Integer epId, @Param("coreEpId") Integer coreEpId);
}
