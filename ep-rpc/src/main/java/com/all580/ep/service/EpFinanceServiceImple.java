package com.all580.ep.service;

import com.all580.ep.api.service.EpFinanceService;
import com.all580.ep.api.service.EpService;
import com.all580.ep.dao.EpMapper;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务操作
 * Created by wxming on 2016/10/21 0021.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class EpFinanceServiceImple implements EpFinanceService {


    @Autowired
    private EpMapper epMapper;

    @Autowired
    private BalancePayService balancePayService;
    @Override
    public Result<Map> getAccountInfoList(Map map) {
        Result<Map> result = new Result<>();
        try {
            List<Map> epList= epMapper.getAccountInfoList(map);//企业信息
            List<Integer> listEpId = new ArrayList<Integer>();//企业id
            Map<String,Map> tempEpMap=new HashMap<String,Map>();//把企业信息以 epId 为key存入用于与余额合并
            Integer core_ep_id = CommonUtil.objectParseInteger(map.get("core_ep_id"));
            for(Map tempMap:epList){
                String epid=tempMap.get("id").toString();
                tempEpMap.put(epid,tempMap);
                listEpId.add(CommonUtil.objectParseInteger(epid));
            }
            List<Map<String, String>> balanceList= balancePayService.getBalanceList(listEpId,core_ep_id).get();
            for(Map<String, String> balance:balanceList){
                balance.putAll(tempEpMap.get(balance.get("ep_id")));//把企业信息合并到余额信息里面
            }
            Map resultMap= new HashMap();
            resultMap.put("list",balanceList);
            resultMap.put("totalCount",epMapper.getAccountInfoListCount(map));
            result.put(map);
            result.setSuccess();
        } catch (Exception e) {
            log.error("数据库查询错误", e);
            throw new ApiException("数据库查询错误", e);
        }
        return result;
    }


    public Result<Map> getAccountInfo(Map map) {

        return null;
    }
}
