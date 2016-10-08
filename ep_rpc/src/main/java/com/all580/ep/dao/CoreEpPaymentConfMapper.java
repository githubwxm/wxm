package com.all580.ep.dao;


import com.all580.ep.api.entity.CoreEpPaymentConf;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpPaymentConfMapper {
  int create(Map params);
  CoreEpPaymentConf findById(Integer core_ep_id);
}
