package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.TicketSysService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Linv2 on 2017-07-06.
 */
@Controller
@RequestMapping(value = "api/ticketSys")
public class TicketSysController extends BaseController {
    @Autowired
    private TicketSysService ticketSysService;


    @RequestMapping(value = "selectTicketSysList", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectTicketSysList(@RequestParam(value = "record_Start", defaultValue = "0") Integer recordStart, @RequestParam(value = "record_Count", defaultValue = "15") Integer recordCount) {
        return ticketSysService.selectTicketSysList(recordStart, recordCount);
    }
}
