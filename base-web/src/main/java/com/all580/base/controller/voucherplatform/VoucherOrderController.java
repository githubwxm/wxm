package com.all580.base.controller.voucherplatform;

import com.all580.voucherplatform.api.service.OrderService;
import com.framework.common.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-27.
 */
@Controller
@RequestMapping(value = "voucherplatform/order")
public class VoucherOrderController {
    @Autowired
    private OrderService voucherOrderService;

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Result get(Integer id, String orderCode) {
        if (id != null) {
            return voucherOrderService.getOrder(id);
        } else if (!StringUtils.isEmpty(orderCode)) {
            return voucherOrderService.getOrder(orderCode);
        } else {
            return new Result(false);
        }
    }


    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ResponseBody
    public Result getOrderCount(Integer platformId,
                                Integer supplyId,
                                String orderCode,
                                String platformOrderId,
                                String mobile,
                                String idNumber,
                                String voucherNumber,
                                Integer status,
                                Date startTime,
                                Date endTime) {
        Integer count = voucherOrderService.getOrderCount(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber, voucherNumber, status, startTime, endTime);
        Result result = new Result(true);
        result.put(count);
        return result;

    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Result getOrderList(Integer platformId,
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
                               @RequestParam(defaultValue = "15")  Integer recordCount) {

        List<Map> mapList = voucherOrderService.getOrderList(platformId, supplyId, orderCode, platformOrderId, mobile, idNumber, voucherNumber, status, startTime, endTime, recordStart, recordCount);
        Result result = new Result(true);
        result.put(mapList);
        return result;

    }

}
