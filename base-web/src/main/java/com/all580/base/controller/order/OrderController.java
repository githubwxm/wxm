package com.all580.base.controller.order;

import com.all580.base.manager.OrderValidateManager;
import com.all580.base.util.Utils;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.api.service.OrderService;
import com.all580.order.api.service.RefundOrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        return bookingOrderService.create(params, "TICKET");
    }

    @RequestMapping(value = "create/pay", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> createPay(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.createValidate());
        return bookingOrderService.create(params, "TICKET_AUTO_PAY");
    }

    @RequestMapping(value = "group/create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> createForGroup(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.createGroupValidate());
        return bookingOrderService.create(params, "TICKET_GROUP");
    }

    @RequestMapping(value = "hotel/create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> createForHotel(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.createHotelValidate());
        return bookingOrderService.create(params, "HOTEL");
    }

    @RequestMapping(value = "hotel/group/create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> createForHotelGroup(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.createHotelGroupValidate());
        return bookingOrderService.create(params, "HOTEL_GROUP");
    }

    @RequestMapping(value = "line/create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> createForLine(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.createLineValidate());
        return bookingOrderService.create(params, "LINE");
    }

    @RequestMapping(value = "package/create", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> createPackageOrder(Map params) throws Exception {

        return bookingOrderService.createPackageOrder(params);
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
        return refundOrderService.apply(params, "TICKET");
    }

    @RequestMapping(value = "refund/ota/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApplyForOta(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundApplyForOtaValidate());
        return refundOrderService.apply(params, "TICKET_OTA");
    }

    @RequestMapping(value = "refund/group/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApplyForGroup(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundApplyForGroupValidate());
        return refundOrderService.apply(params, "TICKET_GROUP");
    }

    @RequestMapping(value = "refund/hotel/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApplyForHotel(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundApplyForHotelValidate());
        return refundOrderService.apply(params, "HOTEL");
    }

    @RequestMapping(value = "refund/line/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApplyForLine(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.refundApplyValidate());
        return refundOrderService.apply(params, "LINE");
    }

    @RequestMapping(value = "refund/package/apply", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> refundApplyForPackage(@RequestBody Map params) throws Exception {
        // 验证参数

        return refundOrderService.refundApplyForPackage(params);
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

    @RequestMapping(value = "resend/group/ticket", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> resendTicketForGroup(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.resendTicketForGroupValidate());
        return bookingOrderService.resendTicketForGroup(params);
    }

    @RequestMapping(value = "modify/group/ticket", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> modifyTicketForGroup(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.modifyTicketForGroupValidate());
        return bookingOrderService.modifyTicketForGroup(params);
    }

    @RequestMapping(value = "consume/hotel/ticket", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> consumeTicketForHotel(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.consumeHotelValidate());
        return bookingOrderService.consumeHotelBySupplier(params);
    }

    @RequestMapping(value = "consume/line/ticket", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> consumeTicketForLine(@RequestBody Map params) throws Exception {
        // 验证参数
        ParamsMapValidate.validate(params, orderValidateManager.consumeLineValidate());
        return bookingOrderService.consumeLineBySupplier(params);
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
        Date[] dates = Utils.checkDate(start_time, end_time);
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
        Date[] dates = Utils.checkDate(start_time, end_time);
        return orderService.selectBySupplierPlatform(supplier_core_ep_id, sale_core_ep_id, date_type,
                dates[0], dates[1], order_status, order_item_status, phone, order_item_number, self, product_sub_number, record_start, record_count);
    }

    @RequestMapping(value = "list/channel/bill")
    @ResponseBody
    public Result<?> listChannelBill(@RequestParam Integer supplier_core_ep_id, String start, String end, Boolean settled) throws Exception {
        Date[] array = Utils.checkDateTime(start, end);
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
        Date[] array = Utils.checkDateTime(start, end);
        Date start_time = array[0];
        Date end_time = array[1];
        return orderService.selectChannelBillForSupplier(supplier_core_ep_id, start_time, end_time, settled);
    }

    @RequestMapping("cancel/times")
    @ResponseBody
    public Result<?> getCancelTimes() {
        return orderService.getCancelTimeout();
    }

    @RequestMapping("sync")
    @ResponseBody
    public Result<?> syncOrder(@RequestParam Long number, String[] accessKeys, String[] tables) {
        return orderService.syncOrder(number, accessKeys, tables);
    }

    @RequestMapping("item/status/info/ota")
    @ResponseBody
    public Result<?> selectOrderItemInfoForOta(@RequestParam Long number) {
        return orderService.selectOrderItemInfoByOta(number);
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
