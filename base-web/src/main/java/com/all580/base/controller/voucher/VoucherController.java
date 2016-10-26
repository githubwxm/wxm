package com.all580.base.controller.voucher;

import com.all580.base.manager.VoucherValidateManager;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        ParamsMapValidate.validate(params, voucherValidateManager.addValidate());
        return voucherRPCService.addVoucher(params.get("name").toString(), params.get("link").toString(), (String) params.get("memo"));
    }

    @RequestMapping(value = "get/id")
    @ResponseBody
    public Result getId(@RequestParam Integer id) {
        return voucherRPCService.selectVoucherById(id);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map params) {
        // 验证参数
        ParamsMapValidate.validate(params, voucherValidateManager.updateValidate());
        int id = Integer.parseInt(params.get("id").toString());
        return voucherRPCService.updateVoucher(id, params.get("name").toString(), params.get("link").toString(), (String) params.get("memo"));
    }

    @RequestMapping(value = "merchant/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addMerchantToVoucher(@RequestBody Map params) {
        // 验证参数
        ParamsMapValidate.validate(params, voucherValidateManager.merchantValidate());
        int epId = Integer.parseInt(params.get("ep_id").toString());
        int voucherId = Integer.parseInt(params.get("voucher_id").toString());
        String accessId = params.get("access_id").toString();
        String accessKey = params.get("access_key").toString();
        String accessName = params.get("access_name").toString();
        return voucherRPCService.addMerchantToVoucher(epId, 0, voucherId, accessId, accessKey, accessName);
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public Result list(Integer record_start, Integer record_count) {
        return voucherRPCService.selectVoucherForList(record_start, record_count);
    }

    @RequestMapping(value = "merchant/list")
    @ResponseBody
    public Result merchantList(Integer target_ep_id, Integer record_start, Integer record_count) {
        return voucherRPCService.selectVoucherOfMerchantForList(target_ep_id, record_start, record_count);
    }
}
