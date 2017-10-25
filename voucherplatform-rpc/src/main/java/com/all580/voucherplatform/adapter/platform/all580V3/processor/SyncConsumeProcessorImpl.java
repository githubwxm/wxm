package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.dao.ConsumeSyncMapper;
import com.all580.voucherplatform.dao.PlatformRoleMapper;
import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.entity.ConsumeSync;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.PlatformRole;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
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
    @Autowired
    private ConsumeSyncMapper consumeSyncMapper;
    @Autowired
    private SupplyMapper supplyMapper;

    @Override
    public Object processor(Platform platform, Map map) {

        String authId = CommonUtil.objectParseString(map.get("mId"));
        String authKey = CommonUtil.objectParseString(map.get("mKey"));
        final PlatformRole platformRole = platformRoleMapper.getRoleByAuthInfo(authId, authKey);
        Assert.notNull(platformRole, "平台商户不存在");
        final Supply supply = supplyMapper.selectByPrimaryKey(platformRole.getSupply_id());
        Assert.notNull(supply, "商户不存在");
        final Map params = new HashMap();
        Object startTime = map.get("startTime");
        params.put("startTime", startTime);
        Object endTime = map.get("endTime");
        params.put("endTime", endTime);
        final ConsumeSync sync = new ConsumeSync();
        sync.setStatus(1);
        sync.setStartTime(DateFormatUtils.converToDateTime(startTime.toString()));
        sync.setEndTime(DateFormatUtils.converToDateTime(endTime.toString()));
        sync.setSupply(platformRole.getSupply_id());
        sync.setCreateTime(new Date());
        consumeSyncMapper.insertSelective(sync);
        params.put("sync", sync.getId());
        asyncService.run(new Runnable() {
            @Override
            public void run() {
               try {
                   SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(supply.getTicketsys_id());
                   if (supplyAdapterService != null) {
                       supplyAdapterService.syncConsume(supply.getId(), params);
                       sync.setStatus(2);
                       sync.setSendTime(new Date());
                       consumeSyncMapper.updateByPrimaryKey(sync);
                   }
               } catch (Throwable e) {
                   log.warn("异步调用失败", e);
                   sync.setStatus(3);
                   consumeSyncMapper.updateByPrimaryKey(sync);
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
