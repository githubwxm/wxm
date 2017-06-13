package com.all580.voucherplatform.adapter.platform.all580V3.processor;

import com.all580.voucherplatform.adapter.ProcessorService;
import com.all580.voucherplatform.api.service.PlatformService;
import com.all580.voucherplatform.dao.PlatformRoleMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.PlatformRole;
import com.all580.voucherplatform.entity.SupplyProduct;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class ImportProdProcessorImpl implements ProcessorService<Platform> {


    private static final String ACTION = "importProduct";
    @Autowired
    private PlatformRoleMapper platformRoleMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;
    @Autowired
    private PlatformService platformService;

    @Override
    public Object processor(Platform platform, Map map) {
        String authId = CommonUtil.objectParseString(map.get("mId"));
        String prodId = CommonUtil.objectParseString(map.get("productId"));
        String prodName = CommonUtil.objectParseString(map.get("productName"));
        String supplyprodId = CommonUtil.objectParseString(map.get("ticketProductId"));
        PlatformRole platformRole = platformRoleMapper.getRoleByAuthInfo(authId, null);
        if (StringUtils.isEmpty(supplyprodId)) {
            throw new ApiException("票务产品Id不能为空");
        }
        if (platformRole == null || platformRole.getPlatform_id() != platform.getId()) {
            throw new ApiException("企业信息不存在");
        }
        if (StringUtils.isEmpty(platformRole.getCode())) {
            throw new ApiException("企业信息未绑定");
        }
        SupplyProduct supplyProd = supplyProductMapper.getSupplyProdByProdId(platformRole.getSupply_id(), supplyprodId);
        if (supplyProd == null) {
            throw new ApiException("票务产品Id传入错误");
        }

        map.put("code", prodId);
        map.put("name", prodName);
        map.put("supplyprodId", supplyProd.getId());


        Result result = platformService.setProd(platformRole.getId(), map);
        if (!result.isSuccess()) {
            throw new ApiException(result.getError());
        } else {
            return null;
        }
    }

    @Override
    public String getAction() {
        return ACTION;
    }
}
