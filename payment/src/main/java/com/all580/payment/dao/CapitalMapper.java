package com.all580.payment.dao;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.entity.Capital;

import java.util.List;

public interface CapitalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Capital record);

    int insertSelective(Capital record);

    Capital selectByPrimaryKey(Integer id);
    List<Capital> selectForUpdateByEpList(List<BalanceChangeInfo> changeInfos);

    int updateByPrimaryKeySelective(Capital record);

    int updateByPrimaryKey(Capital record);

    int batchUpdateById(List<Capital> capitalList);
}