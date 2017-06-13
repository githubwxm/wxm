package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.api.service.PlatformService;
import com.all580.voucherplatform.dao.PlatformRoleMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.PlatformRole;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-08.
 */

@Service
@Slf4j
public class QueryTicketProccessorImpl implements ProcessorService<Platform> {

    private static final String ACTION = "queryTicketProduct";


    @Autowired
    private PlatformRoleMapper platformRoleMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;

    @Override
    public Object processor(Platform platform, Map map) {
        String authId = CommonUtil.objectParseString(map.get("mId"));
        PlatformRole platformRole = platformRoleMapper.getRoleByAuthInfo(authId, null);
        if (platformRole == null || platformRole.getPlatform_id() != platform.getId()) {
            throw new ApiException("企业信息不存在");
        }
        if (StringUtils.isEmpty(platformRole.getCode())) {
            throw new ApiException("企业信息未绑定");
        }
        List<Map> list = supplyProductMapper.getSupplyProdBySupplyId(platformRole.getSupply_id());
        List<Map> mapList = new ArrayList<>();
        for (Map itemMap : list) {
            Map mapRet = new HashMap();
            mapRet.put("productId", itemMap.get("code"));
            mapRet.put("productName", itemMap.get("name"));
            mapRet.put("attachs", itemMap.get("data"));
            mapList.add(mapRet);
        }
        return mapList;
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
