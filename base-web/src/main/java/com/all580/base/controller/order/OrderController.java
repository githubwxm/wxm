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

    @RequestMapping(value = "group/create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> createForGroup(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.createGroupValidate());
        return bookingOrderService.createForGroup(params);
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

    @RequestMapping(value = "refund/group/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApplyForGroup(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundApplyForGroupValidate());
        return refundOrderService.applyForGroup(params);
    }

    @RequestMapping(value = "refund/audit", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundAudit(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundAuditValidate());
        return refundOrderService.audit(params);
    }

    @RequestMapping(value = "refund/money/audit", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundMoneyAudit(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundMoneyAuditValidate());
        return refundOrderService.refundMoneyAudit(params);
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
        checkPlatformOrderParams(start_time, end_time, phone);
        Date[] dates = checkDate(start_time, end_time);
        return orderService.selectPlatformOrderBySupplierCore(supplier_core_ep_id, dates[0], dates[1], order_status,
                order_item_status, phone, order_item_number, record_start, record_count);
    }

    @RequestMapping(value = "list/supplier")
    @ResponseBody
    public Result<?> listSupplierPlatform(@RequestParam Integer supplier_core_ep_id,
                                          Integer sale_core_ep_id,
                                          Integer date_type,
                                          String start_time,
                                          String end_time,
                                          Integer order_status,
                                          Integer order_item_status,
                                          String phone,
                                          Long order_item_number,
                                          Boolean self,
                                          Long product_sub_number,
                                          @RequestParam(defaultValue = "0") Integer record_start,
                                          @RequestParam(defaultValue = "20") Integer record_count) {
        checkPlatformOrderParams(start_time, end_time, phone);
        Date[] dates = checkDate(start_time, end_time);
        return orderService.selectBySupplierPlatform(supplier_core_ep_id, sale_core_ep_id, date_type,
                dates[0], dates[1], order_status, order_item_status, phone, order_item_number, self, product_sub_number, record_start, record_count);
    }

    @RequestMapping(value = "list/channel/bill")
    @ResponseBody
    public Result<?> listChannelBill(@RequestParam Integer supplier_core_ep_id, String start, String end, Boolean settled) throws Exception {
        Date[] array = checkDateTime(start, end);
        Date start_time = array[0];
        Date end_time = array[1];
        return orderService.selectChannelBill(supplier_core_ep_id, start_time, end_time, settled);
    }

    @RequestMapping(value = "list/channel/bill/detail")
    @ResponseBody
    public Result<?> listChannelBillDetail(@RequestParam Integer supplier_core_ep_id,
                                           @RequestParam Integer sale_core_ep_id,
                                           Integer month,
                                           @RequestParam(defaultValue = "0") Integer record_start,
                                           @RequestParam(defaultValue = "20") Integer record_count) {
        return orderService.selectChannelBillDetail(supplier_core_ep_id, sale_core_ep_id, month, record_start, record_count);
    }

    @RequestMapping(value = "list/channel/bill/supplier")
    @ResponseBody
    public Result<?> listChannelBillForSupplier(Integer supplier_core_ep_id, String start, String end, Boolean settled) throws Exception {
        Date[] array = checkDateTime(start, end);
        Date start_time = array[0];
        Date end_time = array[1];
        return orderService.selectChannelBillForSupplier(supplier_core_ep_id, start_time, end_time, settled);
    }

    private Date[] checkDate(String start_time, String end_time) {
        Date[] result = new Date[]{null, null};
        try {
            if (start_time != null) {
                result[0] = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, start_time);
            }
            if (end_time != null) {
                result[1] = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, end_time);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private Date[] checkDateTime(String start, String end) {
        Date start_time = null;
        Date end_time = null;
        try {
            start_time = start == null ? null : DateFormatUtils.parseString(DateFormatUtils.DATE_FORMAT, start);
            end_time = end == null ? null : DateFormatUtils.parseString(DateFormatUtils.DATE_FORMAT, end);
            start_time = start_time == null ? null : DateFormatUtils.setHms(start_time, "00:00:00");
            end_time = end_time == null ? null : DateFormatUtils.setHms(end_time, "23:59:59");
        } catch (Exception e) {
            log.warn("时间格式化异常", e);
        }
        return new Date[]{start_time, end_time};
    }

    private void checkPlatformOrderParams(String start_time, String end_time, String phone) {
        Map<String, Object> params = new HashMap<>();
        params.put("start_time", start_time);
        params.put("end_time", end_time);
        params.put("phone", phone);
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.platformOrderListValidate());
    }
}
