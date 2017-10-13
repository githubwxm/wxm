package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.dao.PlatformRoleMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.PlatformRole;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */
@Service
@Slf4j
public class SyncConsumeProcessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "syncConsume";

    @Autowired
    private PlatformRoleMapper platformRoleMapper;
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;

    @Override
    public Object processor(Platform platform, Map map) {

        String authId = CommonUtil.objectParseString(map.get("mId"));
        String authKey = CommonUtil.objectParseString(map.get("mKey"));
        final PlatformRole platformRole = platformRoleMapper.getRoleByAuthInfo(authId, authKey);
        Assert.notNull(platformRole, "平台商户不存在");
        final Map params = new HashMap();
        params.put("startTime", map.get("startTime"));
        params.put("endTime", map.get("endTime"));
        asyncService.run(new Runnable() {
            @Override
            public void run() {
               try {
                   SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(platformRole.getSupply_id());
                   if (supplyAdapterService != null) {
                       supplyAdapterService.syncConsume(platformRole.getSupply_id(), params);
                   }
               } catch (Throwable e) {
                   log.warn("异步调用失败", e);
               }
            }
        });
        return null;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
