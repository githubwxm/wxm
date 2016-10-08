package com.all580.ep.api.service;


import com.all580.ep.api.entity.CoreEpPaymentConf;
import com.framework.common.Result;

import java.util.List;
import java.util.Map;

public interface CoreEpPaymentConfService {

   /**
    *
    * @param 初始余额配置
    * @return
     */
   Result<Integer> create(Map params);
   Result<Integer> add(Map map);
   CoreEpPaymentConf findById( Integer core_ep_id);
   List<CoreEpPaymentConf> isExists(Map map);
}
