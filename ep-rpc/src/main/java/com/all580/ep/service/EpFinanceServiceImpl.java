package com.all580.ep.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpFinanceService;
import com.all580.ep.api.service.EpService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.EpBankMapper;
import com.all580.ep.dao.EpMapper;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.Result;
import javax.lang.exception.ApiException;

import com.framework.common.lang.StringUtils;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class EpFinanceServiceImpl implements EpFinanceService {

    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;
    @Autowired
    private EpMapper epMapper;

    @Autowired
    private EpBankMapper epBankMapper;

    @Autowired
    private BalancePayService balancePayService;
    @Override
    public Result<Map<String,Object>> selectBank(int id){
        Result<Map<String,Object>> result = new Result<>();
        try{
            result.put(epBankMapper.selectBank(id));
            result.setSuccess();
        }catch(Exception e){
            throw new ApiException("添加银行卡信息异常", e);
        }
        return result;
    }

    @Override
    public Result<Integer> addBank(Map<String,Object> map){
        Result<Integer> result = new Result<>();
        try{
            result.put(epBankMapper.insert(map));
            result.setSuccess();
        }catch(Exception e){
            throw new ApiException("添加银行卡信息异常", e);
        }
        return result;
    }

    @Override
    public Result<Map<String,Object>> getSellerPlatfromAccuntInfo(Map<String,Object> map){
        Result<Map<String,Object>> result = new Result<>();
        Map<String,Object> resultMap= new HashMap<>();
        try {
            Common.checkPage(map);
            List<Map<String,Object>> epList= epMapper.getSellerPlatfromAccuntInfo(map);//企业信息
            if(null==epList||epList.isEmpty()){
                resultMap.put("list",epList);
                resultMap.put("totalCount",0);
                result.put(resultMap);
                result.setSuccess();
                return result;
            }
            List<Integer> listEpId = new ArrayList<Integer>();//企业id
            Map<String,Map> tempEpMap=new HashMap<String,Map>();//把企业信息以 epId 为key存入用于与余额合并
            Integer core_ep_id = CommonUtil.objectParseInteger(map.get("ep_id"));
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
            resultMap.put("totalCount",epMapper.getSellerPlatfromAccuntInfoCount(map));
            result.put(resultMap);
            result.setSuccess();
        } catch (Exception e) {
            log.error("数据库查询错误", e);
            throw new ApiException("数据库查询错误", e);
        }
        return result;
    }
    @Override
    public Result<Map<String,Object>> getAccountInfoList(Map<String,Object> map) {
        Result<Map<String,Object>> result = new Result<>();
        Map<String,Object> resultMap= new HashMap<>();
        try {
            Common.checkPage(map);
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
        Map<String,Object> map = new HashMap<>();
        map.put("ref_id",serialNum);
        map.put(EpConstant.EpKey.CORE_EP_ID,coreEpId);
        map.put("money",balance);
        map.put("ref_type",PaymentConstant.BalanceChangeType.MANUAL_CHANGE_BALANCE_ADD);
        fireBalanceChangedEvent(map);
        return balancePayService.changeBalances(balanceList, PaymentConstant.BalanceChangeType.MANUAL_CHANGE_BALANCE_ADD,serialNum);
    }
    /**
     * 发布余额变更事件
     */
    private void fireBalanceChangedEvent(Map<String,Object> map) {
        log.info("余额充值变更事件----->开始");
        String tag = "core";
        topicPushManager.asyncFireEvent(topicName, tag, PaymentConstant.EVENT_NAME_FUND_CHANGE, map);
        log.info("余额充值变更事件----->成功");
    }
    @Override
    public Result getBalanceSerialList(Integer epId, Integer coreEpId,
                                       String balanceSatatus,String startDate,String endDate,String ref_id,Integer export ,
                                       Integer startRecord, Integer maxRecords,Integer changType) {
//changType   用余提现  暂时未用到
        return balancePayService.getBalanceSerialList(epId,coreEpId,balanceSatatus,startDate,endDate,ref_id,export,startRecord,maxRecords);
    }
}
