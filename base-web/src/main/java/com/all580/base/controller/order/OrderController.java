package com.all580.base.controller.order;

import com.all580.base.manager.OrderValidateManager;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.api.service.RefundOrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
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
    public Result<?> create(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.createValidate());
        return bookingOrderService.create(params);
    }

    @RequestMapping(value = "audit", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> audit(@RequestBody Map params) {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.auditValidate());
        return bookingOrderService.audit(params);
    }

    @RequestMapping(value = "payment", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> payment(@RequestBody Map params) {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.paymentValidate());
        return bookingOrderService.payment(params);
    }

    @RequestMapping(value = "refund/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApply(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundApplyValidate());
        return refundOrderService.apply(params);
    }

    @RequestMapping(value = "refund/audit", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundAudit(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundAuditValidate());
        return refundOrderService.audit(params);
    }

    @RequestMapping(value = "cancel", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> cancel(@RequestBody Map params) {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.cancelValidate());
        return refundOrderService.cancel(params);
    }

    @RequestMapping(value = "cancel/nosplit", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> cancelNoSplit(@RequestBody Map params) {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.cancelValidate());
        return refundOrderService.cancelNoSplit(params);
    }

    @RequestMapping(value = "refund/alipay", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundAliPay(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.cancelValidate());
        return refundOrderService.refundAliPayMoney(params);
    }

    @RequestMapping(value = "resend/ticket", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> resendTicket(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.resendTicketValidate());
        return bookingOrderService.resendTicket(params);
    }
}
