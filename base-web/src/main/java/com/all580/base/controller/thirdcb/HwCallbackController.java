package com.all580.base.controller.thirdcb;

import com.all580.voucher.api.service.third.HwRPCService;
import com.framework.common.lang.StringUtils;
import com.framework.common.lang.codec.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("thirdcb/hw")
public class HwCallbackController {

    @Resource
    HwRPCService hwService;

    @RequestMapping(value="/notify",method= RequestMethod.GET)
    public void notify(
            String consume_id,
            String order_id,
            String voucher_number,
            String consume_amount,
            String vocher_remaining,
            String authkey, HttpServletResponse response) {
        try{
//            String trueAuthKey = MD5Util.MD5(consume_id + order_id + voucher_number + consume_amount + pwd);
            // 这个回调接口。。。没有传客户端id过来，却要拿密码验证加密串，整个小秘书只能有一个好哇的凭证。密码放这先确保能用。
            String trueAuthKey = Md5Utils.getMD5(consume_id + order_id + voucher_number + consume_amount + "C7837164DE40D27DE49DD899A594E9CF").toString();
            if (trueAuthKey.equals(authkey)) {// 签名一致
                if (StringUtils.isBlank(consume_amount))
                    consume_amount = "0";
                if (StringUtils.isBlank(vocher_remaining))
                    consume_amount = "0";
                hwService.consumeOrderCallback(consume_id, order_id, voucher_number, consume_amount, vocher_remaining);
                response.getWriter().write("OK");
            }
            log.error("好哇凭证回调签名不匹配");
        }catch(Exception e){
            log.error("好哇凭证回调出错", e);
        }
    }
}
