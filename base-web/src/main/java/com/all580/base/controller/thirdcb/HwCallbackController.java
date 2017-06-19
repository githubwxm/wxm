package com.all580.base.controller.thirdcb;

import com.all580.voucher.api.service.third.HwRPCService;
import com.framework.common.lang.StringUtils;
import com.framework.common.lang.codec.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("thirdcb/hw")
public class HwCallbackController {

    @Resource
    HwRPCService hwService;

    @Value("${hw.password}")
    String HW_PASSWORD;

    @RequestMapping(value="/notify",method= RequestMethod.GET)
    @ResponseBody
    public String notify(
            String consume_id,
            String order_id,
            String voucher_number,
            String consume_amount,
            String vocher_remaining,
            String authkey) {
        try{
            // 整个小秘书只能配一个好哇帐号
            String trueAuthKey = Md5Utils.getMD5(consume_id + order_id + voucher_number + consume_amount + HW_PASSWORD).toString();
            if (trueAuthKey.equals(authkey)) {// 签名一致
                if (StringUtils.isBlank(consume_amount))
                    consume_amount = "0";
                if (StringUtils.isBlank(vocher_remaining))
                    consume_amount = "0";
                hwService.consumeOrderCallback(consume_id, order_id, voucher_number, consume_amount, vocher_remaining);
                return "OK";
            }
            log.error("好哇凭证回调签名不匹配");
        }catch(Exception e){
            log.error("好哇凭证回调出错", e);
        }
        return null;
    }
}
