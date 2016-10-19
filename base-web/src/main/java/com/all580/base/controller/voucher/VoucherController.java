package com.all580.base.controller.voucher;

import com.all580.base.manager.VoucherValidateManager;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 凭证网关接口
 * @date 2016/10/19 19:51
 */
@Controller
@RequestMapping("api/voucher")
@Slf4j
public class VoucherController extends BaseController {
    @Autowired
    private VoucherValidateManager voucherValidateManager;

    @Autowired
    private VoucherRPCService voucherRPCService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, voucherValidateManager.addValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("添加凭证参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return voucherRPCService.addVoucher(params.get("name").toString(), params.get("link").toString());
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, voucherValidateManager.updateValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改凭证参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            int id = Integer.parseInt(params.get("id").toString());
            return voucherRPCService.updateVoucher(id, params.get("name").toString(), params.get("link").toString());
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "merchant/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addMerchantToVoucher(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, voucherValidateManager.merchantValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("关联凭证商户参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            int epId = Integer.parseInt(params.get("ep_id").toString());
            int voucherId = Integer.parseInt(params.get("voucher_id").toString());
            String accessId = params.get("access_id").toString();
            String accessKey = params.get("access_key").toString();
            String accessName = params.get("access_name").toString();
            return voucherRPCService.addMerchantToVoucher(epId, 0, voucherId, accessId, accessKey, accessName);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public Result list(Integer record_start, Integer record_count) {
        return voucherRPCService.selectVoucherForList(record_start, record_count);
    }
}
