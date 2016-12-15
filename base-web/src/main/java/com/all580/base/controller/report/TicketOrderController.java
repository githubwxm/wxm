package com.all580.base.controller.report;

import com.all580.report.api.service.TicketOrderReportRPCService;
import com.framework.common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("api/report/ticket")
public class TicketOrderController {

    @Resource
    TicketOrderReportRPCService ticketOrderReportRPCService;

    @RequestMapping("order/list")
    @ResponseBody
    public Result<Map<String, Object>> searchTicketOrderBySupplier(
            @RequestParam("ep_id") Integer epId,
            @RequestParam(value = "time_type") Integer timeType,
            @RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate,
            @RequestParam(value = "ticket_type", required = false) Integer ticketType,
            @RequestParam(value = "payment_flag", required = false) Integer paymentFlag,
            @RequestParam("record_start") Integer start,
            @RequestParam("record_count") Integer count
    ) {

        return ticketOrderReportRPCService.searchTicketOrderBySupplier(
                epId,
                timeType,
                startDate,
                endDate,
                ticketType,
                paymentFlag,
                start,
                count
        );
    }

    @RequestMapping("order/report")
    @ResponseBody
    public Result<Map<String, Object>> searchTicketOrderBySupplierReport(
            @RequestParam("ep_id") Integer epId,
            @RequestParam(value = "time_type") Integer timeType,
            @RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate,
            @RequestParam(value = "ticket_type", required = false) Integer ticketType,
            @RequestParam(value = "payment_flag", required = false) Integer paymentFlag
    ) {

        return ticketOrderReportRPCService.searchTicketOrderBySupplierReport(
                epId,
                timeType,
                startDate,
                endDate,
                ticketType,
                paymentFlag
        );
    }
}
