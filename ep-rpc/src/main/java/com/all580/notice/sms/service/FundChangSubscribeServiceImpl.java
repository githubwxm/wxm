package com.all580.notice.sms.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.notice.api.service.FundChangeSubscribeService;
import com.all580.payment.api.service.FundSerialService;
import com.all580.payment.api.service.PlatfromFundService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2016/12/14 0014.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class FundChangSubscribeServiceImpl implements FundChangeSubscribeService {

    @Autowired
    private FundSerialService fundSerialService;
    @Autowired
    private PlatfromFundService platfromFundService;




    public Map<String[], ValidRule[]> generateCreateCreditValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "ref_id",
                "ref_type", //
                "money", //
                "core_ep_id", //
        }, new ValidRule[]{new ValidRule.NotNull()});
        // 校验整数
        rules.put(new String[]{
                "core_ep_id",
                "money", //
                "ref_type", //
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }


    @Override
    public Result process(String s, Map map, Date date) {
        //ParamsMapValidate.validate(map, generateCreateCreditValidate());//
        log.info(" content map  {}",map.toString());
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.putAll(map);
        Integer core_ep_id = CommonUtil.objectParseInteger(map.get(EpConstant.EpKey.CORE_EP_ID));
        Integer moeny = CommonUtil.objectParseInteger( map.get("money"));
        int ref= fundSerialService.selectExists(objectMap).get();
        if(ref==0){
            fundSerialService.insertFundSerial(objectMap);//必须先插入流水再修改总资金
            if(moeny>0){
                platfromFundService.addFund(moeny,core_ep_id);
            }else{
                platfromFundService.exitFund(moeny,core_ep_id);
            }
        }
        return new Result(true);
    }
}
