package com.all580.ep.service;


import com.all580.ep.api.entity.CoreEpPaymentConf;
import com.all580.ep.api.service.CoreEpPaymentConfService;
import com.all580.ep.dao.CoreEpPaymentConfMapper;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class CoreEpPaymentConfServiceImple implements CoreEpPaymentConfService {

    @Autowired
    private CoreEpPaymentConfMapper coreEpChannelMapper;

    @Override
    public int create(Map map) {
        return coreEpChannelMapper.create(map);
    }

    @Override
    public CoreEpPaymentConf findById(Integer core_ep_id) {
        return coreEpChannelMapper.findById(core_ep_id);
    }

}
