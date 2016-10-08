package com.all580.ep.service;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.CoreEpChannelService;
import com.all580.ep.dao.CoreEpAccessMapper;
import com.all580.ep.dao.CoreEpChannelMapper;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class CoreEpChannelServiceImple implements CoreEpChannelService {

    @Autowired
    private CoreEpChannelMapper coreEpChannelMapper;

    @Override
    public Result<?> create(Map params) {
        return null;
    }

}
