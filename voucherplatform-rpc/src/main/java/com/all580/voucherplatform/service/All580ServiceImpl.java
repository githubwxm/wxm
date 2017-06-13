package com.all580.voucherplatform.service;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.service.All580Service;
import com.all580.voucherplatform.dao.PlatformMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class All580ServiceImpl implements All580Service {

    @Autowired
    private AdapterLoadder adapterLoadder;
    @Autowired
    private PlatformMapper platformMapper;
    @Autowired
    private SignInstance signInstance;

    @Override
    public Result process(Map map) {
        String action = CommonUtil.objectParseString(map.get("action"));
        Integer identity = CommonUtil.objectParseInteger(map.get("identity"));
        String signed = CommonUtil.objectParseString(map.get("signed"));
        String content = CommonUtil.objectParseString(map.get("content"));
        Map mapContent = JsonUtils.json2Map(content);
        Platform platform = platformMapper.selectByPrimaryKey(identity);
        if (platform == null) {
            return new Result(false, "身份数据校检失败");
        } else if (!checkSign(platform, content, signed)) {
            return new Result(false, "签名数据校检失败");
        }
        PlatformAdapterService platformAdapterService = adapterLoadder.getPlatformAdapterService(platform);
        return platformAdapterService.process(action, platform, mapContent);
    }

    private boolean checkSign(Platform platform, String source, String signed) {
        SignService signService = signInstance.getSignService(platform.getSignType());
        if (signService == null) {
            return true;
        }
        SignKey signKey = new SignKey();
        signKey.setPublicKey(platform.getPublicKey());
        signKey.setPrivateKey(platform.getPrivateKey());
        return signService.verifySign(signKey, source, signed);
    }
}
