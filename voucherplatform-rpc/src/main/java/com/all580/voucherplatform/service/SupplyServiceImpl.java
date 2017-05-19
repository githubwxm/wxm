package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.SupplyService;
import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.entity.SupplyProduct;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-19.
 */
public class SupplyServiceImpl implements SupplyService {
    @Autowired
    private SignInstance signInstance;
    @Autowired
    private SupplyMapper supplyMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;

    @Override
    public Result create(Map map) {
        Supply supply = JsonUtils.map2obj(map, Supply.class);
        SignService signService = signInstance.getSignService(supply.getSignType());
        SignKey signKey = signService.generate();
        supply.setPrivateKey(signKey.getPrivateKey());
        supply.setPublicKey(signKey.getPublicKey());
        supplyMapper.insertSelective(supply);
        return new Result(true);
    }

    @Override
    public Result getSupply(Integer id) {
        Result result = new Result(true);
        Supply supply = supplyMapper.selectByPrimaryKey(id);
        if (supply != null) {
            result.put(JsonUtils.obj2map(supply));
        }
        return result;
    }

    @Override
    public int getCount(String name) {
        return 0;
    }

    @Override
    public Result getList(String name, Integer recordStart, Integer recordCount) {
        return null;
    }

    @Override
    public Result getProd(int supplyId, String prodId) {
        Result result = new Result(true);
        Map map = getProdMap(supplyId, prodId);
        if (map != null) {
            result.put(map);
        }
        return result;
    }

    /**
     * 获取供应商的一个产品
     *
     * @param supplyId
     * @param prodId
     * @return
     */
    private Map getProdMap(int supplyId, String prodId) {
        return supplyProductMapper.getSupplyProdByProdId(supplyId, prodId);
    }

    @Override
    public Result setProd(int supplyId, Map map) {
        String prodId = CommonUtil.objectParseString(map.get("code"));
        Map prodMap = getProdMap(supplyId, prodId);
        if (prodMap == null) {//先判断数据库是否存在改数据
            SupplyProduct supplyProduct = JsonUtils.map2obj(map, SupplyProduct.class);
            supplyProduct.setStatus(true);
            supplyProduct.setSupply_id(supplyId);
            if (map.containsKey("data")) {
                supplyProduct.setData(JsonUtils.toJson(map.get("data")));
            }
            supplyProductMapper.insertSelective(supplyProduct);
        } else {
            //如果存在就修改
            SupplyProduct supplyProduct = JsonUtils.map2obj(map, SupplyProduct.class);
            if (map.containsKey("data")) {
                supplyProduct.setData(JsonUtils.toJson(map.get("data")));
            }
            supplyProduct.setId(CommonUtil.objectParseInteger("id"));
            supplyProductMapper.updateByPrimaryKeySelective(supplyProduct);
        }
        return new Result(true);
    }

    @Override
    public Result delProd(int supplyId, String prodId) {
        Map map = getProdMap(supplyId, prodId);
        Integer id = CommonUtil.objectParseInteger(map.get("id"));
        if (id == null) {
            return new Result(false);
        }
        SupplyProduct supplyProduct = new SupplyProduct();
        supplyProduct.setId(id);
        supplyProduct.setStatus(false);
        supplyProductMapper.updateByPrimaryKeySelective(supplyProduct);
        return new Result(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Result setProd(int supplyId, List<Map> mapList) {
        for (Map map : mapList) {
            setProd(supplyId, map);
        }
        return new Result(true);
    }
}
