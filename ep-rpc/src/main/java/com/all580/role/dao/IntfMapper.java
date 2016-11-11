package com.all580.role.dao;

import com.all580.role.entity.Intf;

import java.util.List;
import java.util.Map;

public interface IntfMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Intf record);

    int insertSelective(Intf record);

    Intf selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Intf record);

    int updateByPrimaryKey(Intf record);

    /**
     * @param params  name,auth   --  ,status
     * @return
     */
    int insertIntf(Map<String,Object> params);

    /**
     * 查询菜单下的所有
     */
    List<Map<String,Object>> selectFuncId(Long id);
}