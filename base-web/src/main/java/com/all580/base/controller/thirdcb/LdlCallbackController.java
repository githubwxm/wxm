package com.all580.base.controller.thirdcb;

import com.all580.voucher.api.service.third.LdlRPCService;
import com.framework.common.lang.StringUtils;
import com.framework.common.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("thirdcb/ldl")
public class LdlCallbackController {

    @Resource
    LdlRPCService ldlService;

    private static final String UPDATE_PRODUCT_CB_ACTION = "product_change";

    private static final String VERIFY_ORDER_CB_ACTION = "order_verify";

    private static final String REFUND_ORDER_CB_ACTION = "order_refund";

    /**
     *　旅动力回调入口
     * @param params
     * @return
     */
    @RequestMapping(value = "facade", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> facade(@RequestBody Map<String, Object> params) {
        if (params.get("action") != null && params.get("client_id") != null
                && StringUtils.isNumeric(params.get("client_id").toString())
                && params.get("key") != null)
        if (UPDATE_PRODUCT_CB_ACTION.equals(params.get(("action"))))
            ldlService.updateProductCallback(params.get("key").toString(),
                params.get("client_id").toString(),
                params.get("action").toString(),
                (Map<String, Object>) params.get("body"));
        else if (VERIFY_ORDER_CB_ACTION.equals(params.get("action")))
            ldlService.validOrderCallback(params.get("key").toString(),
                params.get("client_id").toString(),
                params.get("action").toString(),
                (Map<String, Object>) params.get("body"));
        else if (REFUND_ORDER_CB_ACTION.equals(params.get("action")))
            ldlService.refundOrderCallback(params.get("key").toString(),
                params.get("client_id").toString(),
                params.get("action").toString(),
                (Map<String, Object>) params.get("body"));
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("describe", "success");
        return map;
    }
}
