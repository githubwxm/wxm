package com.all580.order.dao;

import com.all580.order.entity.RefundPackageAccount;

/**
 * Created by xiangzw on 2017/7/24.
 */
public interface RefundPackageAccountMapper {

    int insertSelective(RefundPackageAccount record);

    int updateByPrimaryKeySelective(RefundPackageAccount record);
}
