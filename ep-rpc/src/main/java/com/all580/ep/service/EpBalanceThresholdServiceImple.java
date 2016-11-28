package com.all580.ep.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.EpBalanceThresholdMapper;
import com.all580.ep.dao.EpMapper;
import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.framework.common.Result;
import javax.lang.exception.ApiException;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class EpBalanceThresholdServiceImple implements EpBalanceThresholdService {

    @Autowired
    private EpBalanceThresholdMapper epBalanceThresholdMapper;

    @Autowired
    private EpMapper epMapper;

    @Autowired
    private SmsService smsService;

    @Override
    public  Result<Integer> createOrUpdate(Map<String,Object> map) {
        Result<Integer> result = new Result<>();
        try {
            //   map.put("threshold",CommonUtil.objectParseInteger(map.get("threshold")));
            String threshold = CommonUtil.objectParseString(map.get("threshold"));
            if(threshold!=null){
                if(threshold.length()>7){
                    throw new ApiException("余额阀值太大超出范围");
                }else{
                    Integer num = CommonUtil.objectParseInteger(threshold);
                    map.put("threshold",num*100);
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
    public Result warn(Map<String,Object> map) {
        Result returnResult = new Result();
        try {
            Result<Map<String,Object>> result = select(map);
            Integer balance=Common.objectParseInteger(map.get("balance"));//传来的余额
            Integer threshold = Common.objectParseInteger(result.get("threshold"));//阀值
            if(threshold==null){
                return new Result(true);
            }
            if(null==result.get()){
                log.error("未查询到阀值数据"+map);
                throw new ApiException("添加更新余额阀值出错"+map);
            }
            if(balance<threshold){
                //Todo  发送余额短信
                Map<String,Object> epMap = new HashMap<>();//EpConstant.EpKey.CORE_EP_ID
                Integer ep_id= CommonUtil.objectParseInteger(map.get("id"));//企业id
                epMap.put("id",ep_id);//再看发送短信所需要的参数
                 List<Map<String,Object>> list= epMapper.select(epMap);//   获取企业信息
                 if(null!=list && !list.isEmpty()){
                     Map<String,Object> mapResult = list.get(0);
                     ep_id=CommonUtil.objectParseInteger( map.get(EpConstant.EpKey.CORE_EP_ID));//发送短信人
                    String destPhoneNum=  mapResult.get("link_phone").toString();
                     Map<String, String>  params = new HashMap<>();
                     params.put("qiye",mapResult.get("name").toString());
                     String jinqian =threshold+"";
                     String point = Common.matcher(jinqian,"([\\d]{2}$)" );//Common.matcher(125665419+"","([\\d]{2}$)" )
                     params.put("jinqian",jinqian.replaceAll("([\\d]{2}$)", "."+point));
                   return  smsService.send(destPhoneNum, SmsType.Ep.BALANCE_SHORTAGE,ep_id,params);//发送短信
                 }
            }
        } catch (Exception e) {
            log.error("查询数据库出错", e);
            throw new ApiException("查询数据库出错", e);
        }
        return new Result(true);
    }

}
