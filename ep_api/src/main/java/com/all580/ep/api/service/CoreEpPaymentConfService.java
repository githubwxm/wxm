package com.all580.ep.api.service;


import com.all580.ep.api.entity.CoreEpPaymentConf;
import com.framework.common.Result;

import java.util.List;
import java.util.Map;

public interface CoreEpPaymentConfService {

   int create(Map params);
   CoreEpPaymentConf findById(Integer core_ep_id);
}
