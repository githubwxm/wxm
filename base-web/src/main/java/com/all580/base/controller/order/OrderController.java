package com.all580.base.controller.order;

import com.all580.base.manager.OrderValidateManager;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.api.service.OrderService;
import com.all580.order.api.service.RefundOrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
    @Autowired
    private OrderService orderService;

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
        ParamsMapValidate.validate(params, orderValidateManager.refundAlipayValidate());
        return refundOrderService.refundAliPayMoney(params);
    }

    @RequestMapping(value = "resend/ticket", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> resendTicket(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.resendTicketValidate());
        return bookingOrderService.resendTicket(params);
    }

    @RequestMapping(value = "platform/list/supplier")
    @ResponseBody
    public Result<?> listPlatformOrderBySupplierCore(@RequestParam Integer supplier_core_ep_id,
                                                     String start_time,
                                                     String end_time,
                                                     Integer order_status,
                                                     Integer order_item_status,
                                                     String phone,
                                                     Long order_item_number,
                                                     @RequestParam(defaultValue = "0") Integer record_start,
                                                     @RequestParam(defaultValue = "20") Integer record_count) {
        Map<String, Object> params = new HashMap<>();
        params.put("start_time", start_time);
        params.put("end_time", end_time);
        params.put("phone", phone);
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.platformOrderListValidate());
        Date startTime = null;
        Date endTime = null;
        try {
            if (start_time != null) {
                startTime = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, start_time);
            }
            if (end_time != null) {
                endTime = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, end_time);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return orderService.selectPlatformOrderBySupplierCore(supplier_core_ep_id, startTime, endTime, order_status,
                order_item_status, phone, order_item_number, record_start, record_count);
    }
}
