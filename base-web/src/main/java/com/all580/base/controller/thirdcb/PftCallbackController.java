package com.all580.base.controller.thirdcb;


import com.all580.voucher.api.conf.VoucherConstant;
import com.all580.voucher.api.service.third.PftRPCService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("thirdcb/pft")
public class PftCallbackController {

    @Resource
    PftRPCService pftService;

//    @RequestMapping(value = "notify", method = RequestMethod.POST)
//    @ResponseBody
//    public String notify(@RequestBody Map<String, Object> params) {
//        Result<String> result = pftService.pftOrderStateCallback(
//                VoucherConstant.Provider.PFT,
//                params.get("VerifyCode") == null? null : params.get("VerifyCode").toString(),
//                params.get("Order16U") == null? null : params.get("Order16U").toString(),
//                params.get("ActionTime") == null? null : params.get("ActionTime").toString(),
//                params.get("OrderCall") == null? null : params.get("OrderCall").toString(),
//                params.get("Tnumber") == null? null : params.get("Tnumber").toString(),
//                params.get("OrderState") == null? null : params.get("OrderState").toString(),
//                params.get("Refundtype") == null? null : params.get("Refundtype").toString(),
//                params.get("Explain") == null? null : params.get("Explain").toString(),
//                params.get("Source") == null? null : params.get("Source").toString(),
//                params.get("Action") == null? null : params.get("Action").toString(),
//                params.get("AllCheckNum") == null? null : params.get("AllCheckNum").toString(),
//                params.get("RefundAmount") == null? null : params.get("RefundAmount").toString(),
//                params.get("RefundFee") == null? null : params.get("RefundFee").toString());
//        if (result == null) throw new ApiException("票付通服务回调接口调用失败");
//        return result.get();
//    }

    @RequestMapping(value = "notify", method = RequestMethod.POST)
    @ResponseBody
    public String notify(HttpServletRequest request) {
        log.error("pft_key:{}", request.getParameterMap().keySet().toArray()[0]);
        log.error("pft_value:{}", request.getParameterMap().get(request.getParameterMap().keySet().toArray()[0])[0]);
        if (!request.getContentType().startsWith("application/x-www-form-urlencoded") || request.getParameterMap().isEmpty()) {
            return "";
        }
//        Map<String, String[]> params = request.getParameterMap();
        String json = request.getParameterMap().keySet().toArray()[0].toString();
        Map<String, String> params = JsonUtils.json2Map(json);
        Result<String> result = pftService.pftOrderStateCallback(
                VoucherConstant.Provider.PFT,
                params.get("VerifyCode") == null? null : params.get("VerifyCode"),
                params.get("Order16U") == null? null : params.get("Order16U"),
                params.get("ActionTime") == null? null : params.get("ActionTime"),
                params.get("OrderCall") == null? null : params.get("OrderCall"),
                params.get("Tnumber") == null? null : params.get("Tnumber"),
                params.get("OrderState") == null? null : params.get("OrderState"),
                params.get("Refundtype") == null? null : params.get("Refundtype"),
                params.get("Explain") == null? null : params.get("Explain"),
                params.get("Source") == null? null : params.get("Source"),
                params.get("Action") == null? null : params.get("Action"),
                params.get("AllCheckNum") == null? null : params.get("AllCheckNum"),
                params.get("RefundAmount") == null? null : params.get("RefundAmount"),
                params.get("RefundFee") == null? null : params.get("RefundFee"));
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
