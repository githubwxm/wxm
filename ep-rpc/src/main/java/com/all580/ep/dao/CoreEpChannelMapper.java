package com.all580.ep.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpChannelMapper {
   int selectChannel(Map<String,Object> params);
   int create(Map<String,Object> params);
   int update(Map<String,Object> params);
   List<Map<String,Object>> select(Map<String,Object> params);
   List<Map<String,Object>>selectSupplierCoreEpId();
   int cancel(@Param("id") Integer id);
   int selectCount(Map<String,Object> map);
   Integer selectPlatfromRate(@Param("supplier_core_ep_id") int supplier_core_ep_id
           ,@Param("seller_core_ep_id") int seller_core_ep_id);
}
