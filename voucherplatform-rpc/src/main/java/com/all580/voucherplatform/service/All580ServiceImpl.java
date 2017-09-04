package com.all580.voucherplatform.service;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.api.service.All580Service;
import com.all580.voucherplatform.dao.PlatformMapper;
import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Service
@Slf4j
public class All580ServiceImpl implements All580Service {

    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private PlatformMapper platformMapper;
    @Autowired
    private SupplyMapper supplyMapper;
    @Autowired
    private SignInstance signInstance;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    public Result process(Map map) {
        String action = CommonUtil.objectParseString(map.get("action"));
        Integer identity = CommonUtil.objectParseInteger(map.get("identity"));
        String signed = CommonUtil.objectParseString(map.get("signed"));
        String content = CommonUtil.objectParseString(map.get("content"));
        Map mapContent = getMapFormContent(content);
        log.info("identity={},action={},content={},signed={}", new Object[]{identity, action, content, signed});
        Platform platform = platformMapper.selectByPrimaryKey(identity);
        if (platform == null) {
            return new Result(false, "身份数据校检失败");
        } else if (!signInstance.checkSign(platform.getSignType(), platform.getPublicKey(), platform.getPrivateKey(),
                content, signed)) {
            return new Result(false, "签名数据校检失败");
        }
        PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platform);
        return platformAdapterService.process(action, platform, mapContent);
    }

    @Override
    public Result supplyProcess(Map map) {
        String action = CommonUtil.objectParseString(map.get("action"));
        Integer identity = CommonUtil.objectParseInteger(map.get("identity"));
        String signed = CommonUtil.objectParseString(map.get("signed"));
        String content = CommonUtil.objectParseString(map.get("content"));
        Map mapContent = getMapFormContent(content);
        log.info("identity={},action={},content={},signed={}", new Object[]{identity, action, content, signed});

        Supply supply = supplyMapper.selectByPrimaryKey(identity);
        if (supply == null) {
            return new Result(false, "身份数据校检失败");
        } else if (!signInstance.checkSign(supply.getSignType(), supply.getPublicKey(), supply.getPrivateKey(), content,
                signed)) {
            // return new Result(false, "签名数据校检失败");
        }
        SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(supply);
        return supplyAdapterService.process(action, supply, mapContent);
    }

    private Map getMapFormContent(String content) {
        Map mapContent = null;
        if (content.startsWith("[") && content.endsWith("]")) {
            List list = JsonUtils.json2List(content);
            mapContent = new HashMap();
            mapContent.put("data", list);
        } else {
            mapContent = JsonUtils.json2Map(content);
        }
        return mapContent;
    }
}
