package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.OrderService;
import com.framework.common.Result;
import com.framework.common.vo.PageRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-27.
 */
@Controller
@RequestMapping(value = "api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Result get(Integer id,
                      String orderCode) {
        if (id != null) {
            return orderService.getOrder(id);
        } else if (!StringUtils.isEmpty(orderCode)) {
            return orderService.getOrder(orderCode);
        } else {
            return new Result(false);
        }
    }

    @RequestMapping(value = "selectOrderList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectOrderList(Integer platformId,
                                                   Integer supplyId,
                                                   String orderCode,
                                                   String platformOrderId,
                                                   String mobile,
                                                   String idNumber,
                                                   String voucherNumber,
                                                   Integer status,
                                                   Date startTime,
                                                   Date endTime,
                                                   @RequestParam(defaultValue = "0") Integer recordStart,
                                                   @RequestParam(defaultValue = "15") Integer recordCount) {

        return orderService.selectOrderList(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber,
                voucherNumber, status, startTime, endTime, recordStart, recordCount);

    }

    @RequestMapping(value = "selectOrderConsumeList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectOrderConsumeList(Integer platformId,
                                                          Integer supplyId,
                                                          String orderCode,
                                                          String platformOrderId,
                                                          String mobile,
                                                          String idNumber,
                                                          String voucherNumber,
                                                          Integer status,
                                                          Date startTime,
                                                          Date endTime,
                                                          String consumeCode,
                                                          String supplyConsumeCode,
                                                          String deviceId,
                                                          Integer orderId,
                                                          @RequestParam(defaultValue = "0") Integer recordStart,
                                                          @RequestParam(defaultValue = "15") Integer recordCount) {
        return orderService.selectOrderConsumeList(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber,
                voucherNumber, status, startTime, endTime, consumeCode, supplyConsumeCode, deviceId, orderId,
                recordStart, recordCount);
    }

    @RequestMapping(value = "selectOrderRefundList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectOrderRefundList(Integer platformId,
                                                         Integer supplyId,
                                                         String orderCode,
                                                         String platformOrderId,
                                                         String mobile,
                                                         String idNumber,
                                                         String voucherNumber,
                                                         Integer status,
                                                         Date startTime,
                                                         Date endTime,
                                                         String platformRefId,
                                                         String voucherRefId,
                                                         String supplyRefId,
                                                         Integer orderId,
                                                         Integer prodType,
                                                         @RequestParam(defaultValue = "0") Integer recordStart,
                                                         @RequestParam(defaultValue = "15") Integer recordCount) {
        return orderService.selectOrderRefundList(platformId, supplyId, orderCode, platformOrderId, mobile,
                idNumber, voucherNumber, status, startTime, endTime, platformRefId, voucherRefId,
                supplyRefId,
                orderId, prodType, recordStart, recordCount);
    }


    @RequestMapping(value = "selectGroupOrderList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectGroupOrderList(Integer platformId,
                                                        Integer supplyId,
                                                        String orderCode,
                                                        String platformOrderId,
                                                        String mobile,
                                                        String idNumber,
                                                        String voucherNumber,
                                                        Integer status,
                                                        Date startTime,
                                                        Date endTime,
                                                        @RequestParam(defaultValue = "0") Integer recordStart,
                                                        @RequestParam(defaultValue = "15") Integer recordCount) {
        return orderService.selectGroupOrderList(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber,
                voucherNumber, status, startTime, endTime, recordStart, recordCount);
    }

    @RequestMapping(value = "reConsume", method = RequestMethod.GET)
    @ResponseBody
    public Result reConsume(@RequestParam Integer consumeId) {
        return orderService.reConsume(consumeId);
    }


}
