package com.all580.ep.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.EpBalanceThresholdMapper;
import com.all580.ep.dao.EpMapper;
import com.all580.ep.dao.SmsSendMapper;
import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.framework.common.Result;
import javax.lang.exception.ApiException;
import com.framework.common.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class EpBalanceThresholdServiceImpl implements EpBalanceThresholdService {

    @Autowired
    private EpBalanceThresholdMapper epBalanceThresholdMapper;

    @Autowired
    private EpMapper epMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    private SmsSendMapper smsSendMapper;

    @Override
    public  Result<Integer> createOrUpdate(Map<String,Object> map) {
        Result<Integer> result = new Result<>();
        try {
            //   map.put("threshold",CommonUtil.objectParseInteger(map.get("threshold")));//
            String threshold = CommonUtil.objectParseString(map.get("threshold"));
            if(threshold!=null){
                if(threshold.length()>7){
                    throw new ApiException("余额阀值太大超出范围");
                }else{
                   // Integer num = CommonUtil.objectParseInteger(threshold);
                    try{
                        double m = Double.parseDouble(CommonUtil.objectParseString(threshold))*100;
                        int num =(int)m;
                        map.put("threshold",num);
                    }catch (NumberFormatException e){
                        throw new ApiException("余额阀值格式错误");
                    }

                }
            }
            if(map.get("isChannel")==null){//添加通道汇率的时候不用检验是否一致
                Integer id =CommonUtil.objectParseInteger(map.get("id")) ;
                Integer core_ep_id=  epMapper.selectPlatformId(id);
                if(!core_ep_id.equals(id)){
                    if(!core_ep_id.equals(map.get(EpConstant.EpKey.CORE_EP_ID))){
                        throw new ApiException("企业不属于该平台商");
                    }
                }
            }
            result.put(epBalanceThresholdMapper.createOrUpdate(map));
            result.setSuccess();
        }catch (ApiException e) {
            log.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("添加更新余额阀值出错", e);
            throw new ApiException("添加更新余额阀值出错", e);
        }
        return result;
    }

    @Override
    public  Result<Map<String,Object>> selectBalance(Map<String,Object> map) {
        Result<Map<String,Object>> result = new Result<>();
        try {
            result.put(epBalanceThresholdMapper.selectBalance(map));
            result.setSuccess();
        } catch (Exception e) {
            log.error("查询余额阀值出错", e);
            throw new ApiException("查询余额阀值出错", e);
        }
        return result;
    }



    @Override
    public  Result<Map<String,Object>> select(Map<String,Object> map) {
        Result<Map<String,Object>> result = new Result<>();
        try {
            result.put(epBalanceThresholdMapper.select(map));
            result.setSuccess();
        } catch (Exception e) {
            log.error("查询余额阀值出错", e);
            throw new ApiException("查询余额阀值出错", e);
        }
        return result;
    }

    @Override
    public Result selectThresholdList(Map<String,Object> map){
        Result result = new Result(true);
        Common.checkPage(map);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("totalCount",smsSendMapper.selectThresholdCount(map));
        resultMap.put("list",smsSendMapper.selectThresholdList(map));
        result.put(resultMap);
        return result;
    }

    @Override
    public Result CreateOnUpdateThreshold(Map<String,Object> map){
        Result result = new Result(true);
        Integer id = CommonUtil.objectParseInteger(map.get("id"));//send_ep_id
        if(null==id){
          if("1".equals(map.get("threshold_status"))){
              String send_phone1= CommonUtil.objectParseString( map.get("send_phone1"));
              String send_phone2= CommonUtil.objectParseString( map.get("send_phone2"));
              Integer threshold=CommonUtil.objectParseInteger( map.get("threshold"));
              Assert.notNull(threshold, "余额提醒值为空");
              if(send_phone1==null && send_phone2==null){
                   result.setError("电话号码必须有一个");
                  return result;
              }
              if(send_phone1!=null && send_phone2!=null){
                  if(send_phone1.trim().equals(send_phone2)){
                      result.setError("2个电话号码不能一致");
                      return result;
                  }
              }
              if(threshold<0){
                  result.setError("余额提醒值为负数");
                  return result;
              }
              if(threshold>5000000){
                  result.setError("余额提醒值过大，50000");
                  return result;
              }
           }
            smsSendMapper.insert(map);
        }else{
            smsSendMapper.updateByPrimaryKey(map);
        }
        Map<String,Object> thresholdMap = new HashMap<>();
        thresholdMap.put("id",map.get("send_ep_id"));
        thresholdMap.put(EpConstant.EpKey.CORE_EP_ID,map.get(EpConstant.EpKey.CORE_EP_ID)) ;
        Integer threshold =CommonUtil.objectParseInteger( map.get("threshold"));
        thresholdMap.put("threshold",threshold);
        epBalanceThresholdMapper.createOrUpdate(thresholdMap);
        return result;
    }
    @Override
    public Result warn(Map<String,Object> map) {
       // Result returnResult = new Result();
        try {
            Result<Map<String,Object>> result = select(map);
            Integer balance=Common.objectParseInteger(map.get("balance"));//传来的余额
            Integer threshold = Common.objectParseInteger(result.get("threshold"));//阀值
            Integer history_balance=Common.objectParseInteger(map.get("history_balance"));
            if(threshold==null){
                return new Result(true);
            }
            if(null==result.get()){
                log.error("未查询到阀值数据"+map);
                throw new ApiException("添加更新余额阀值出错"+map);
            }
            if(balance<threshold && history_balance>=threshold){
                Map<String,Object> epMap = new HashMap<>();//EpConstant.EpKey.CORE_EP_ID
                Integer ep_id= CommonUtil.objectParseInteger(map.get("id"));//企业id
                epMap.put("id",ep_id);//再看发送短信所需要的参数
                 List<Map<String,Object>> list= epMapper.select(epMap);//   获取企业信息
                 if(null!=list && !list.isEmpty()){
                     Map<String,Object> mapResult = list.get(0);
                   Integer  core_ep_id=CommonUtil.objectParseInteger( map.get(EpConstant.EpKey.CORE_EP_ID));//发送短信人
                    String destPhoneNum=  mapResult.get("link_phone").toString();
                     Map<String, String>  params = new HashMap<>();
                     params.put("qiye",mapResult.get("name").toString());
                     String jinqian =threshold+"";
                     if(jinqian.length()>2){
                         String point = Common.matcher(jinqian,"([\\d]{2}$)" );//Common.matcher(125665419+"","([\\d]{2}$)" )
                         params.put("jinqian",jinqian.replaceAll("([\\d]{2}$)", "."+point));
                     }else if(jinqian.length()==2){
                         params.put("jinqian","0."+jinqian);
                     }else if(jinqian.length()==1){
                         params.put("jinqian","0.0"+jinqian);
                     }
                       Map<String,Object> mapSend = smsSendMapper.selectByEpId(ep_id);//   余额修改之后的发送短信
                     if(null!=mapSend && "1".equals(CommonUtil.objectParseString( mapSend.get("threshold_status")))){
                         String send_phone2=CommonUtil.objectParseString(mapSend.get("send_phone2"));
                         if( null != send_phone2 && !"".equals(send_phone2.trim())){
                             smsService.send(send_phone2, SmsType.Ep.BALANCE_SHORTAGE,core_ep_id,params);//发送短信
                         }
                         String send_phone1=CommonUtil.objectParseString(mapSend.get("send_phone1"));
                         if(  null != send_phone1 && !"".equals(send_phone1.trim())){
                             return smsService.send(send_phone1, SmsType.Ep.BALANCE_SHORTAGE,core_ep_id,params);//发送短信
                         }else{
                             return  smsService.send(destPhoneNum, SmsType.Ep.BALANCE_SHORTAGE,core_ep_id,params);//发送短信
                         }
                     }
                 }
            }
        } catch (Exception e) {
            log.error("查询数据库出错", e);
            throw new ApiException("查询数据库出错", e);
        }
        return new Result(true);
    }

}
