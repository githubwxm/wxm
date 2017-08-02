package com.all580.voucherplatform.service;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.supply.SupplyAdapterService;
import com.all580.voucherplatform.api.service.SupplyService;
import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.dao.TicketSysMapper;
import com.all580.voucherplatform.entity.Supply;
import com.all580.voucherplatform.entity.SupplyProduct;
import com.all580.voucherplatform.entity.TicketSys;
import com.all580.voucherplatform.utils.sign.SignInstance;
import com.all580.voucherplatform.utils.sign.SignKey;
import com.all580.voucherplatform.utils.sign.SignService;
import com.all580.voucherplatform.utils.sign.SignType;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.*;

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
    private TicketSysMapper ticketSysMapper;
    @Autowired
    private SupplyProductMapper supplyProductMapper;
    @Autowired
    private AdapterLoader adapterLoader;

    @Override
    public Result create(Map map) {
        Supply supply = JsonUtils.map2obj(map, Supply.class);
        SignService signService = signInstance.getSignService(supply.getSignType());
        SignKey signKey = signService.generate();
        supply.setSignType(signService.getSignType().getValue());
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
    public Result<PageRecord<Map>> selectSupplyList(String name, Integer recordStart, Integer recordCount) {

        PageRecord<Map> record = new PageRecord<>();
        int count = supplyMapper.selectSupplyCount(name);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = supplyMapper.selectSupplyList(name, recordStart, recordCount);
            record.setList(list);
        } else {

            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result selectSupply(Integer supplyId) {
        Map map = supplyMapper.selectSupplyMapByPrimaryKeySelective(supplyId);
        Result result = new Result(true);
        result.put(map);
        return result;
    }

    @Override
    public Result updateSignType(Map map) {
        Integer supplyId = CommonUtil.objectParseInteger(map.get("id"));
        Integer signType = CommonUtil.objectParseInteger(map.get("signType"));
        SignService signService = signInstance.getSignService(signType);
        SignKey signKey = signService.generate();

        Supply supply = new Supply();
        supply.setId(supplyId);
        supply.setSignType(signService.getSignType().getValue());
        supply.setPrivateKey(signKey.getPrivateKey());
        supply.setPublicKey(signKey.getPublicKey());
        supplyMapper.updateByPrimaryKeySelective(supply);
        return new Result(true);
    }

    /**
     * 根据供应商的id，获取产品信息
     *
     * @param supplyId
     * @return
     */
    @Override
    public Result<PageRecord<Map>> selectSupplyProdList(Integer supplyId, String prodCode, Integer recordStart, Integer recordCount) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = supplyProductMapper.selectSupplyProdCount(supplyId, prodCode);
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(supplyProductMapper.selectSupplyProdList(supplyId, prodCode, recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }

    @Override
    public Map getProd(int supplyId, String prodId) {
        SupplyProduct supplyProduct = getProdMap(supplyId, prodId);
        return JsonUtils.obj2map(supplyProduct);
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
    public Boolean checkProdPower(int supplyId) {
        Supply supply = supplyMapper.selectByPrimaryKey(supplyId);
        if (supply == null) {
            log.info("检测供应商产品维护权限，{}，供应商不存在", new Object[]{supply.getTicketsys_id()});
            return false;
        }
        if (supply.getTicketsys_id() == null || supply.getTicketsys_id() < 1) {
            log.info("检测供应商产品维护权限，{}，供应商未绑定票务系统", new Object[]{supply.getTicketsys_id()});
        }
        TicketSys ticketSys = ticketSysMapper.selectByPrimaryKey(supply.getTicketsys_id());
        if (ticketSys == null) {
            log.error("检测供应商产品维护权限，{}，票务验证系统不存在", new Object[]{supply.getTicketsys_id()});
            return false;
        }
        return ticketSys.getProdAddType();
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
            updateProd.setName(CommonUtil.objectParseString(map.get("name")));
            updateProd.setDescription(CommonUtil.objectParseString(map.get("description")));
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

    @Override
    public Result syncProd(int supplyId) {
        Supply supply = supplyMapper.selectByPrimaryKey(supplyId);
        if (supply == null) {
            return new Result(false, "供应商不存在");
        }
        SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(supply);
        supplyAdapterService.queryProd(supplyId);
        return new Result(true);
    }

    @Override
    public Map getConfFile(int supplyId) {
        Supply supply = supplyMapper.selectByPrimaryKey(supplyId);
        if (supply == null) {
            throw new ApiException("参数错误");
        }
        SupplyAdapterService supplyAdapterService = adapterLoader.getSupplyAdapterService(supply);
        return supplyAdapterService.getConf(supplyId);
    }

    @Override
    public List<Map> getSignType() {
        List<Map> mapList = new ArrayList<>();
        for (SignType signType : SignType.values()) {
            Map map = new HashMap();
            map.put("name", signType.toString());
            map.put("value", signType.getValue());
            mapList.add(map);
        }
        return mapList;
    }
}
