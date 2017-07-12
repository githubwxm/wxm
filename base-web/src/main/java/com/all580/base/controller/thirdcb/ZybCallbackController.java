package com.all580.base.controller.thirdcb;

import com.all580.order.api.service.OrderService;
import com.all580.voucher.api.conf.VoucherConstant;
import com.all580.voucher.api.service.VoucherRPCService;
import com.all580.voucher.api.service.third.ZybRPCService;
import com.framework.common.Result;
import com.framework.common.lang.StringUtils;
import com.framework.common.lang.codec.Md5Utils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("thirdcb/zyb")
public class ZybCallbackController {

    @Resource
    OrderService orderService;

    @Resource
    VoucherRPCService voucherRPCService;

    @Resource
    ZybRPCService zybService;

    @RequestMapping(value = "notify", method = RequestMethod.GET)
    @ResponseBody
    public String notify(String order_no,
                         String status,
                         String sub_order_no,
                         String checkNum,
                         String returnNum,
                         String total,
                         String checkTime,
                         String sign) throws UnsupportedEncodingException {
        if (!signOk(order_no, sign)) return "";
        Map<String, Object> callbackParam = new HashMap<>();
        callbackParam.put("checkNum", checkNum);
        callbackParam.put("checkTime", URLDecoder.decode(checkTime, "UTF-8"));
        if (zybService.orderValidCallback(callbackParam) != null) {
            return "success";
        }
        return "";
    };

    @RequestMapping(value = "refund", method = RequestMethod.GET)
    @ResponseBody
    public String refund(String order_no,
                         String retreatBatchNo,
                         String sub_order_no,
                         String auditStatus,
                         String returnNum,
                         String sign) throws UnsupportedEncodingException {
        if (!signOk(order_no, sign)) return "";
        Map<String, Object> callbackParam = new HashMap<>();
        callbackParam.put("returnNum", returnNum);
        callbackParam.put("auditStatus", auditStatus);
        if (zybService.refundTicketCallback(callbackParam) != null) {
            return "success";
        }
        return "";
    };

    private boolean signOk(String order_no, final String sign) {
        // 订单号实际为子订单号必须为长整
        if (!StringUtils.isNumeric(order_no) || order_no.length() != 16) return false;
        Result<Map> orderItemResult = orderService.selectOrderItemByNumber(Long.parseLong(order_no));
        // 未找到子订单
        if (orderItemResult == null || orderItemResult.get() == null) return false;
        Object supplierEpId = orderItemResult.get().get("supplier_ep_id");
        if (supplierEpId == null) return false;
        Result<List> masResult = voucherRPCService.selectMa(CommonUtil.objectParseInteger(supplierEpId), VoucherConstant.Provider.ZYB);
        if (masResult == null || masResult.get() == null || masResult.get().isEmpty()) return false;
        if (masResult.get().stream().filter(map -> Md5Utils.getMD5("order_code=" + order_no + ((Map) map).get("access_key").toString().split("\\|")[1]).equals(sign)).count() > 0) {
            return true;
        }
        return false;
    }
}
