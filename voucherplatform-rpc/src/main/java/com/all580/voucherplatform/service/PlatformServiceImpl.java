package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.PlatformService;
import com.all580.voucherplatform.dao.*;
import com.all580.voucherplatform.entity.*;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Linv2 on 2017-05-23.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class PlatformServiceImpl implements PlatformService {
    @Autowired
    private SignInstance signInstance;
    @Autowired
    private PlatformMapper platformMapper;
    @Autowired
    private PlatformRoleMapper platformRoleMapper;
    @Autowired
    private PlatformProductMapper prodMapper;
    @Autowired
    private SupplyMapper supplyMapper;


    private Integer defaultPordType = 1;

    @Override
    public Result create(Map map) {
        Platform platform = JsonUtils.map2obj(map, Platform.class);
        SignService signService = signInstance.getSignService(platform.getSignType());
        SignKey signKey = signService.generate();
        platform.setPrivateKey(signKey.getPrivateKey());
        platform.setPublicKey(signKey.getPublicKey());
        platform.setCreateTime(new Date());
        platform.setStatus(true);
        platformMapper.insertSelective(platform);
        return new Result(platform.getId() > 0);
    }

    @Override
    public Result update(Map map) {
        Platform platform = new Platform();
        platform.setId(CommonUtil.objectParseInteger(map.get("id")));
        platform.setName(CommonUtil.objectParseString(map.get("name")));
        platform.setDescription(CommonUtil.objectParseString(map.get("description")));
        Integer signType = CommonUtil.objectParseInteger(map.get("signType"));
        if (signType != null) {
            SignService signService = signInstance.getSignService(signType);
            platform.setSignType(signType);
            SignKey signKey = signService.generate();
            platform.setPrivateKey(signKey.getPrivateKey());
            platform.setPublicKey(signKey.getPublicKey());
        }
        int platformId = platformMapper.updateByPrimaryKeySelective(platform);

        return new Result(platformId > 0);
    }

    @Override
    public Result createRole(Integer platformId, Integer supplyId) {
        Result result = new Result(false);
        PlatformRole platformRole = getRoleByPlatformIdAndSupplyId(platformId, supplyId);
        if (platformRole != null) {
            result.setError("同一供应商不能对平台商多次授权！");
            return result;
        }
        platformRole = new PlatformRole();
        platformRole.setAuthId(UUID.randomUUID().toString());
        platformRole.setAuthKey(UUID.randomUUID().toString());
        platformRole.setPlatform_id(platformId);
        platformRole.setSupply_id(supplyId);
        platformRole.setCreateTime(new Date());
        platformRoleMapper.insertSelective(platformRole);
        result.setSuccess(true);
        return result;
    }

    private PlatformRole getRoleByPlatformIdAndSupplyId(Integer platformId, Integer supplyId) {
        return platformRoleMapper.getRoleByPlatformIdAndSupplyId(platformId, supplyId);
    }

    private PlatformRole getRoleByAuthInfo(String authId, String authKey) {
        return platformRoleMapper.getRoleByAuthInfo(authId, authKey);
    }


    @Override
    public Result auth(Map map) {
        Result result = new Result(false);
        String authId = CommonUtil.objectParseString(map.get("authId"));
        String authKey = CommonUtil.objectParseString(map.get("authKey"));
        PlatformRole platformRole = getRoleByAuthInfo(authId, authKey);
        if (platformRole == null) {
            result.setError("商户认证信息错误");
            return result;
        }
        if (StringUtils.isEmpty(platformRole.getName())) {
            platformRole.setAuthTime(new Date());
        } else {
            platformRole.setModifyTime(new Date());
        }
        String name = CommonUtil.objectParseString(map.get("name"));
        if (StringUtils.isEmpty(name)) {
            Supply supply = supplyMapper.selectByPrimaryKey(platformRole.getSupply_id());
            name = supply.getName();
        }
        platformRole.setName(name);
        platformRole.setCode(CommonUtil.objectParseString(map.get("code")));
        platformRoleMapper.updateByPrimaryKeySelective(platformRole);
        result.setSuccess(true);
        result.put(JsonUtils.obj2map(platformRole));
        return result;
    }

    @Override
    public Map getPlatform(Integer id) {
        Platform platform = platformMapper.selectByPrimaryKey(id);
        return JsonUtils.obj2map(platform);
    }

    @Override
    public Result setProd(int roleId, Map map) {
        PlatformRole platformRole = platformRoleMapper.selectByPrimaryKey(roleId);
        String code = CommonUtil.objectParseString(map.get("code"));
        if (StringUtils.isEmpty(code)) {
            throw new ApiException("参数不完整，缺少参数code");
        }
        PlatformProduct platformProduct = prodMapper.getProdByPlatform(platformRole.getPlatform_id(), platformRole.getSupply_id(), code);
        if (platformProduct == null) {
            platformProduct = new PlatformProduct();
            platformProduct.setPlatformrole_id(platformRole.getId());
            platformProduct.setCode(CommonUtil.objectParseString(map.get("code")));
            platformProduct.setName(CommonUtil.objectParseString(map.get("name")));
            platformProduct.setProducttype_id(map.containsKey("productTypeId") ? CommonUtil.objectParseInteger(map.get("productTypeId")) : defaultPordType);
            platformProduct.setPlatform_id(platformRole.getPlatform_id());
            platformProduct.setSupply_id(platformRole.getSupply_id());
            platformProduct.setSupplyprod_id(CommonUtil.objectParseInteger(map.get("supplyprodId")));
            platformProduct.setStatus(true);
            platformProduct.setCreateTime(new Date());
            prodMapper.insertSelective(platformProduct);
        } else {
            platformProduct.setModifyTime(new Date());
            platformProduct.setProducttype_id(CommonUtil.objectParseInteger(map.get("productTypeId")));
            platformProduct.setName(CommonUtil.objectParseString(map.get("name")));
            platformProduct.setSupplyprod_id(CommonUtil.objectParseInteger(map.get("supplyprodId")));

            prodMapper.updateByPrimaryKey(platformProduct);
        }
        return new Result(true);
    }


    @Override
    public Map getProd(int prodId) {
        PlatformProduct platformProduct = prodMapper.selectByPrimaryKey(prodId);
        return JsonUtils.obj2map(platformProduct);
    }

    @Override
    public Result getProdByPlatform(int platformId, String prodCode) {
        Result result = new Result(true);
        PlatformProduct platformProduct = prodMapper.getProdByPlatform(platformId, null, prodCode);
        if (platformProduct != null) {
            result.put(JsonUtils.obj2map(platformProduct));
        }
        return result;
    }

    @Override
    public int getProdCount(String name, Integer platformId, Integer supplyId, Integer supplyprodId, String platformProdCode, Integer productTypeId) {
        return prodMapper.getProdCount(name, platformId, supplyId, supplyprodId, platformProdCode, productTypeId);
    }

    @Override
    public List<Map> getProdList(String name, Integer platformId, Integer supplyId, Integer supplyprodId, String platformProdCode, Integer productTypeId, Integer recordStart, Integer recordCount) {
        return prodMapper.getProdList(name, platformId, supplyId, supplyprodId, platformProdCode, productTypeId, recordStart, recordCount);
    }

    @Override
    public int getPlatformCount(String name) {
        return platformMapper.getPlatformCount(name);
    }

    @Override
    public List<Map> getPlatformtList(String name, Integer recordStart, Integer recordCount) {
        return platformMapper.getPlatformtList(name, recordStart, recordCount);
    }
}
