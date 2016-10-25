package com.all580.ep.service;

import com.all580.ep.api.service.LogCreditService;
import com.all580.ep.dao.LogCreditMapper;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授信
 * Created by wxming on 2016/10/19 0019.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class LogCreditServiceImple implements LogCreditService{

    @Autowired
    private LogCreditMapper logCreditMapper;

    @Autowired
    private BalancePayService balancePayService;

    @Override
    public Result<Integer> select(Integer ep_id,Integer core_ep_id) {
        Result<Integer> result = new Result<Integer>();
        try {

            result.put( logCreditMapper.select(ep_id,core_ep_id));
            result.setSuccess();
        } catch (Exception e) {
            // log.error("创建中心平台接口访问配置", e);
            throw new ApiException("查询授信异常", e);
        }
        return result;
    }

    @Override
    public Result<Integer> create(Map map) {
        Result<Integer> result = new Result<Integer>();
        try {
            Integer ep_id = CommonUtil.objectParseInteger(map.get("ep_id")) ;
            Integer core_ep_id=CommonUtil.objectParseInteger(map.get("core_ep_id")) ;
            Integer credit_before= logCreditMapper.select(ep_id,core_ep_id);
            map.put("credit_before",credit_before==null?0:credit_before);
            result.put(logCreditMapper.create(map));
            Integer credit_after = CommonUtil.objectParseInteger(map.get("credit_after"));
            //balancePayService. //修改余额里的授信  ep_id   core_ep_id   credit_after
            balancePayService.setCredit(ep_id,core_ep_id,credit_after);
            result.setSuccess();
        } catch (Exception e) {
            // log.error("创建中心平台接口访问配置", e);
            throw new ApiException("创建中心平台接口访问配置", e);
        }
        return result;
    }

    @Override
    public Result<Map> selectList(Map map) {
        Result<Map> result = new Result<>(true);
        Map resultMap = new HashMap();
        try {
            CommonUtil.checkPage(map);
            List<Map> list=  logCreditMapper.selectList(map);
            int count=     logCreditMapper.selectListCount(map);
            resultMap.put("list",list);
            resultMap.put("totalCount",count);
            result.put(resultMap);
            result.setSuccess();
        } catch (Exception e) {
            // log.error("创建中心平台接口访问配置", e);
            throw new ApiException("查询授信列表出错", e);
        }
        return result;
    }

    @Override
    public Result<Map> hostoryCredit(Map params) {
        Result<Map> result = new Result<>(true);
        try {
            Map map =new HashMap();
            map.put("list",logCreditMapper.hostoryCredit(params));
            result.put(map);
        } catch (Exception e) {
            // log.error("创建中心平台接口访问配置", e);
            throw new ApiException("查询历史授信列表出错", e);
        }
        return result;
    }

}
