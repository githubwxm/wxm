package com.all580.ep.dao;


import com.all580.ep.api.entity.CoreEpPaymentConf;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpPaymentConfMapper {
  int create(Map params);
  CoreEpPaymentConf findById( @Param("core_ep_id") Integer core_ep_id);
  List<CoreEpPaymentConf> isExists(Map map);
  int updateStatus(Map map);
}
