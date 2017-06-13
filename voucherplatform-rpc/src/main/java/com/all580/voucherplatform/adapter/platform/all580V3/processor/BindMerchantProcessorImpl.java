package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.api.service.PlatformService;
import com.all580.voucherplatform.entity.Platform;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Service
@Slf4j
public class BindMerchantProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "bindMerchant";

    @Autowired
    private PlatformService platformService;

    @Override
    public Object processor(Platform platform, Map map) {

        String authId = CommonUtil.objectParseString(map.get("mId"));
        String authKey = CommonUtil.objectParseString(map.get("mKey"));
        String code = CommonUtil.objectParseString(map.get("otaMerchantId"));
        Map mapParam = new HashMap();
        mapParam.put("authId", authId);
        mapParam.put("authKey", authKey);
        mapParam.put("code", code);
        Result result = platformService.auth(mapParam);
        if (!result.isSuccess()) {
            throw new ApiException(result.getError());
        } else {
            Map mapRet = new HashMap();
            mapRet.put("merchantName", ((Map) result.getResult()).get("name"));
            return mapRet;
        }
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
