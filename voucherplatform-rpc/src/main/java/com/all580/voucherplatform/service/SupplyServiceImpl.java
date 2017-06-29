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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-19.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
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
        supply.setCreateTime(new Date());
        supply.setStatus(true);
        supply.setTicketsys_id(0);
        supplyMapper.insertSelective(supply);
        return new Result(true);
    }

    /**
     * 修改供应商信息
     *
     * @param map {id:xx,name:xx,address:xx,region:xx,description:xx}
     *            id  -int    -要修改的供应商id
     *            name    -string -名字
     *            address -string -地址
     *            region    -string -区域
     *            description   -string -描述
     * @return
     */
    @Override
    public Result update(Map map) {
        Supply supply = new Supply();
        supply.setId(CommonUtil.objectParseInteger(map.get("id")));
        supply.setName(CommonUtil.objectParseString(map.get("name")));
        supply.setPhone(CommonUtil.objectParseString(map.get("phone")));
        supply.setAddress(CommonUtil.objectParseString(map.get("address")));
        supply.setRegion(CommonUtil.objectParseString(map.get("region")));
        supply.setDescription(CommonUtil.objectParseString(map.get("description")));
        supplyMapper.updateByPrimaryKeySelective(supply);
        return new Result(true);
    }

    /**
     * 修改配置
     *
     * @param map {id:xx,conf:xx}
     *            id  -int    -要修改的供应商id
     *            conf  -string -配置数据
     * @return
     */
    @Override
    public Result updateConf(Map map) {
        Supply supply = new Supply();
        supply.setId(CommonUtil.objectParseInteger(map.get("id")));
        supply.setConf(CommonUtil.objectParseString(map.get("conf")));
        supplyMapper.updateByPrimaryKeySelective(supply);
        return new Result(true);
    }

    /**
     * 修改票务对接信息
     *
     * @param map {id:xx,ticketsysId:xx}
     *            id  -int    -要修改的供应商id
     *            ticketsysId  -int -票务系统id
     * @return
     */
    @Override
    public Result updateTicketSys(Map map) {
        Supply supply = new Supply();
        supply.setId(CommonUtil.objectParseInteger(map.get("id")));
        supply.setTicketsys_id(CommonUtil.objectParseInteger(map.get("ticketsysId")));
        supplyMapper.updateByPrimaryKeySelective(supply);
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


    /**
     * 根据供应商的id，获取产品信息
     *
     * @param supplyId
     * @return
     */
    @Override
    public Result getProdList(int supplyId) {
        Result result = new Result(true);
        result.put(supplyProductMapper.getSupplyProdBySupplyId(supplyId));
        return result;
    }

    @Override
    public Result getProd(int supplyId, String prodId) {
        Result result = new Result(true);
        SupplyProduct supplyProduct = getProdMap(supplyId, prodId);
        if (supplyProduct != null) {
            result.put(JsonUtils.obj2map(supplyProduct));
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
    private SupplyProduct getProdMap(int supplyId, String prodId) {
        return supplyProductMapper.getSupplyProdByProdId(supplyId, prodId);
    }

    @Override
    public Result setProd(int supplyId, Map map) {
        String prodId = CommonUtil.objectParseString(map.get("code"));
        SupplyProduct supplyProduct = getProdMap(supplyId, prodId);
        if (supplyProduct == null) {//先判断数据库是否存在改数据
            supplyProduct = new SupplyProduct();
            supplyProduct.setCode(prodId);
            supplyProduct.setName(CommonUtil.objectParseString(map.get("name")));
            supplyProduct.setData(CommonUtil.objectParseString(map.get("data")));
            supplyProduct.setStatus(true);
            supplyProduct.setCreateTime(new Date());
            supplyProduct.setSyncTime(new Date());
            supplyProduct.setSupply_id(supplyId);
            supplyProductMapper.insertSelective(supplyProduct);
        } else {
            //如果存在就修改
            SupplyProduct updateProd = new SupplyProduct();
            updateProd.setName(CommonUtil.objectParseString("name"));
            updateProd.setDescription(CommonUtil.objectParseString("description"));
            updateProd.setSyncTime(new Date());
            updateProd.setId(supplyProduct.getId());
            supplyProductMapper.updateByPrimaryKeySelective(updateProd);
        }
        return new Result(true);
    }

    @Override
    public Result delProd(int supplyId, String prodId) {
        SupplyProduct supplyProduct = getProdMap(supplyId, prodId);
        if (supplyProduct == null) {
            return new Result(false);
        }
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
