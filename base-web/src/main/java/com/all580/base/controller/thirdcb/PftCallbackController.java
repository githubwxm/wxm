package com.all580.base.controller.thirdcb;


import com.all580.voucher.api.conf.VoucherConstant;
import com.all580.voucher.api.service.third.PftRPCService;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("thirdcb/pft")
public class PftCallbackController {

    @Resource
    PftRPCService pftService;

    @RequestMapping(value = "notify", method = RequestMethod.POST)
    @ResponseBody
    public String notify(@RequestBody Map<String, Object> params) {
        Result<String> result = pftService.pftOrderStateCallback(
                VoucherConstant.Provider.PFT,
                params.get("VerifyCode") == null? null : params.get("VerifyCode").toString(),
                params.get("Order16U") == null? null : params.get("Order16U").toString(),
                params.get("ActionTime") == null? null : params.get("ActionTime").toString(),
                params.get("OrderCall") == null? null : params.get("OrderCall").toString(),
                params.get("Tnumber") == null? null : params.get("Tnumber").toString(),
                params.get("OrderState") == null? null : params.get("OrderState").toString(),
                params.get("Refundtype") == null? null : params.get("Refundtype").toString(),
                params.get("Explain") == null? null : params.get("Explain").toString(),
                params.get("Source") == null? null : params.get("Source").toString(),
                params.get("Action") == null? null : params.get("Action").toString(),
                params.get("AllCheckNum") == null? null : params.get("AllCheckNum").toString(),
                params.get("RefundAmount") == null? null : params.get("RefundAmount").toString(),
                params.get("RefundFee") == null? null : params.get("RefundFee").toString());
        if (result == null) throw new ApiException("票付通服务回调接口调用失败");
        return result.get();
    }

    @RequestMapping(value = "notify", method = RequestMethod.GET)
    @ResponseBody
    public String getNotify() {
        // 提供给票付通校验回调地址用
        return "success";
    }
}
