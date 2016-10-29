package com.all580.base.controller.payment;

import com.all580.payment.api.service.BalancePayService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 余额账户网关
 *
 * @author panyi on 2016/10/25.
 * @since V0.0.1
 */
@Controller
@RequestMapping(value = "api/balance")
public class BalanceController extends BaseController {
    @Autowired
    private BalancePayService balancePayService;

    @RequestMapping(value = "info/{epId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getBalance(@PathVariable("epId") Integer epId) {
        Result<Map<String, String>> result = new Result<>();
        try {
            Integer coreEpId = CommonUtil.objectParseInteger(getAttribute("core_ep_id"));
            if (epId == null || coreEpId == null) {
                result.setFail();
                result.setError("缺少参数");
            } else {
                result = balancePayService.getBalanceAccountInfo(epId, coreEpId);
            }
        } catch (Exception e) {
            String msg = "获取余额账户信息出错，原因：" + e.getMessage();
            logger.error(msg, e);
            result.setFail();
            result.setError(msg);
        }
        return result;
    }

}
