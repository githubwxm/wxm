package com.all580.ep.service;

import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.EpBalanceThresholdMapper;
import com.all580.ep.dao.EpMapper;
import com.all580.notice.api.service.SmsService;
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
    public  Result<Integer> createOrUpdate(Map map) {
        Result<Integer> result = new Result<>();
        try {
            result.put(epBalanceThresholdMapper.createOrUpdate(map));
            result.setSuccess();
        } catch (Exception e) {
            log.error("添加更新余额阀值出错", e);
            throw new ApiException("添加更新余额阀值出错", e);
        }
        return result;
    }



    @Override
    public  Result<Map> select(Map map) {
        Result<Map> result = new Result<>();
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
    public boolean warn(Map map) {
        try {
            Result<Map> result = select(map);
            Integer balance=Common.objectParseInteger(map.get("balance"));//传来的余额
            Integer threshold = Common.objectParseInteger(result.get("threshold"));//阀值
            if(null==result.get()){
                log.error("未查询到阀值数据"+map);
                throw new ApiException("添加更新余额阀值出错"+map);
            }
            if(balance<threshold){
                //Todo  发送余额短信
                Map epMap = new HashMap();
                Integer ep_id= CommonUtil.objectParseInteger(map.get("ep_id"));
                epMap.put("id",ep_id);//再看发送短信所需要的参数
                 List<Map> list= epMapper.select(epMap);//   获取企业信息
                 if(null==list){
                     if(!list.isEmpty()){
                         Map mapResult = list.get(0);
                        String destPhoneNum=  mapResult.get("link_phone").toString();
                         return smsService.send(destPhoneNum,-1,ep_id,null);//发送短信
                     }
                 }
                //TODO  发送之后操作

            }
        } catch (Exception e) {
            log.error("查询数据库出错", e);
            throw new ApiException("查询数据库出错", e);
        }
        return false;
    }

}
