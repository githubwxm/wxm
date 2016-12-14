package com.all580.ep.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface PlatfromFundMapper {

    Map<String,Object> selectPlatfromFund(int core_ep_id);

    int insertPlatfromFund(int core_ep_id);

    /**
     *  资金支出
     * @param money
     * @param core_ep_id
     * @return
     */
    int exitFund(@Param("money") int money,@Param("core_ep_id") int core_ep_id );

    /**
     * 资金收入
     * @param money
     * @param core_ep_id
     * @return
     */
    int addFund(@Param("money") int money,@Param("core_ep_id") int core_ep_id );
}