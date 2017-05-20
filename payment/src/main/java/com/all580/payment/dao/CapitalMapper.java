package com.all580.payment.dao;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.entity.Capital;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CapitalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Capital record);

    int insertSelective(Capital record);

    Capital selectByPrimaryKey(Integer id);
    Capital selectByEpIdAndCoreEpId(@Param("ep_id")Integer epId, @Param("core_ep_id")Integer coreEpId);

    List<Capital> selectForUpdateByEpList(@Param("changeInfos") List<BalanceChangeInfo> changeInfos);
    List<Map<String,String>> listByEpIdAndCoreEpId(@Param("epIdList") List<Integer> epIdList,@Param("core_ep_id")
                                                   Integer coreEpId);
    List<Map<String,Object>> listCapitalAll();
    int updateByPrimaryKeySelective(Capital record);

    int updateByPrimaryKey(Capital record);

    int updateByEpIdAndCoreEpId(Capital record);

    int batchUpdateById(@Param("capitalList") List<Capital> capitalList);
}