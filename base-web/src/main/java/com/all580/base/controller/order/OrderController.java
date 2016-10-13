package com.all580.base.controller.order;

import com.all580.base.manager.OrderValidateManager;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.api.service.RefundOrderService;
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
 * @Description: 订单网关
 * @date 2016/10/11 10:52
 */
@Controller
@RequestMapping("api/order")
@Slf4j
public class OrderController extends BaseController {
    @Autowired
    private OrderValidateManager orderValidateManager;

    @Autowired
    private BookingOrderService bookingOrderService;
    @Autowired
    private RefundOrderService refundOrderService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> create(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, orderValidateManager.createValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("创建订单参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return bookingOrderService.create(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "audit", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> audit(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, orderValidateManager.auditValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("审核订单参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return bookingOrderService.audit(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "payment", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> payment(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, orderValidateManager.paymentValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("订单支付参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return bookingOrderService.payment(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "refund/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApply(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, orderValidateManager.refundApplyValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("订单退订申请参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return refundOrderService.apply(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "refund/audit", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundAudit(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, orderValidateManager.refundAuditValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("订单退订审核参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return refundOrderService.audit(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }

    @RequestMapping(value = "cancel", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> cancel(@RequestBody Map params) {
        // 验证参数
        try {
            ParamsMapValidate.validate(params, orderValidateManager.cancelValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("订单取消参数验证失败", e);
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        try {
            return refundOrderService.cancel(params);
        } catch (ApiException e) {
            return new Result<>(false, e.getCode(), e.getMsg());
        }
    }
}
