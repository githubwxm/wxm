package com.all580.ep.service;

import com.all580.ep.api.service.EpFinanceService;
import com.all580.ep.api.service.EpService;
import com.all580.ep.dao.EpMapper;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import javax.lang.exception.ApiException;

import com.framework.common.lang.StringUtils;
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
    public Result<Map<String,Object>> getAccountInfoList(Map<String,Object> map) {
        Result<Map<String,Object>> result = new Result<>();
        Map<String,Object> resultMap= new HashMap<>();
        try {
            List<Map<String,Object>> epList= epMapper.getAccountInfoList(map);//企业信息
            if(null==epList||epList.isEmpty()){
                resultMap.put("list",epList);
                resultMap.put("totalCount",0);
                result.put(resultMap);
                result.setSuccess();
                return result;
            }
            List<Integer> listEpId = new ArrayList<Integer>();//企业id
            Map<String,Map> tempEpMap=new HashMap<String,Map>();//把企业信息以 epId 为key存入用于与余额合并
            Integer core_ep_id = CommonUtil.objectParseInteger(map.get("core_ep_id"));
            for(Map<String,Object> tempMap:epList){
                String epid=tempMap.get("id").toString();
                tempEpMap.put(epid,tempMap);
                listEpId.add(CommonUtil.objectParseInteger(epid));
            }
            List<Map<String, String>> balanceList= balancePayService.getBalanceList(listEpId,core_ep_id).get();
            for(Map<String, String> balance:balanceList){
                String tempss = String.valueOf(balance.get("ep_id"));
                Map temp=tempEpMap.get(tempss);
                balance.putAll(temp);//把企业信息合并到余额信息里面
            }

            resultMap.put("list",balanceList);
            resultMap.put("totalCount",epMapper.getAccountInfoListCount(map));
            result.put(resultMap);
            result.setSuccess();
        } catch (Exception e) {
            log.error("数据库查询错误", e);
            throw new ApiException("数据库查询错误", e);
        }
        return result;
    }


    @Override
    public Result<Map<String, String>> getBalanceAccountInfo(Integer epId, Integer coreEpId) {

        return balancePayService.getBalanceAccountInfo(epId,coreEpId);
    }

    @Override
    public Result addBalance(Integer epId,Integer coreEpId,Integer balance){
        List<BalanceChangeInfo> balanceList=new ArrayList<>();
        BalanceChangeInfo b= new BalanceChangeInfo();
        b.setBalance(balance);
        b.setCan_cash(balance);
        b.setEp_id(epId);
        b.setCore_ep_id(coreEpId);
        balanceList.add(b);
        String  serialNum=System.currentTimeMillis()+"";
        return balancePayService.changeBalances(balanceList, PaymentConstant.BalanceChangeType.MANUAL_CHANGE_BALANCE,serialNum);
    }
    @Override
    public Result getBalanceSerialList(Integer epId, Integer coreEpId,
                                       String balanceSatatus,String startDate,String endDate,String ref_id,
                                       Integer startRecord, Integer maxRecords,Integer changType) {
//changType   用余提现  暂时未用到
        return balancePayService.getBalanceSerialList(epId,coreEpId,balanceSatatus,startDate,endDate,ref_id,startRecord,maxRecords);
    }
}
