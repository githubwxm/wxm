package com.all580.base.controller.order;

import com.all580.order.api.service.SyncExceptionOrder;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/4/6 0006.
 */
@Controller
@RequestMapping("api/exception/order")
@Slf4j
public class OrderExceptionContoller {
    @Autowired
    private SyncExceptionOrder syncExceptionOrder;

    @RequestMapping(value = "exception/list")
    @ResponseBody
    public Result<?> selectOrderException( String supplier_ep_name,
                                            String buy_ep_name,
                                           Long number,String start,String end,
                                           @RequestParam(defaultValue = "0") Integer record_start,
                                           @RequestParam(defaultValue = "20") Integer record_count) {
        Map<String,Object> map = new HashMap<>();
        map.put("supplier_ep_name",supplier_ep_name);
        map.put("buy_ep_name",buy_ep_name);
        map.put("number",number);
        map.put("start",start);
        map.put("end",end);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
        return syncExceptionOrder.selectOrderException(map);
    }
    @RequestMapping(value = "select/sync")
    @ResponseBody
    public Result<?> selectSyncOrder(@RequestParam(value = "number")  Long number) {
        return syncExceptionOrder.selectSyncOrder(number);
    }

    @RequestMapping(value = "sync/order/all", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> SyncOrderAll(@RequestBody Map params) {
       String number= CommonUtil.objectParseString(params.get("number"));
        Assert.notNull(number);
        return syncExceptionOrder.SyncOrderAll(Long.valueOf(number));
    }
}
