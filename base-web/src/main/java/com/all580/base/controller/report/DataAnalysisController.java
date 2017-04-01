package com.all580.base.controller.report;

import com.all580.base.controller.payment.BalanceController;
import com.all580.base.util.Utils;
import com.all580.report.api.ReportConstant;
import com.all580.report.api.service.DataAnalysisService;
import com.framework.common.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.lang.exception.ApiException;
import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 数据分析
 * @date 2017/3/31 17:26
 */
@Controller
@RequestMapping("api/report/analysis")
public class DataAnalysisController extends BalanceController {
    @Autowired
    private DataAnalysisService dataAnalysisService;

    @RequestMapping("sale/consume/list")
    @ResponseBody
    public Result<?> consumeListBySale(Integer product_id,
                                       @RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam Integer core_ep_id,
                                       String sortCol,
                                       String sortType,
                                       @RequestParam(defaultValue = "0") Integer record_start,
                                       @RequestParam(defaultValue = "20") Integer record_count) {
        Date[] dates = Utils.checkDate(start, end);
        if (!Utils.isSortType(sortType)) {
            throw new ApiException("排序方式错误");
        }
        String[] sortCols = new String[]{"individual", "team", "total"};
        if (!ArrayUtils.contains(sortCols, sortCol)) {
            throw new ApiException("排序字段错误");
        }
        return dataAnalysisService.selectConsumeAnalysisListBySale(product_id, dates[0], dates[1], core_ep_id, sortCol, sortType, record_start, record_count);
    }

    @RequestMapping("sale/consume/compared")
    @ResponseBody
    public Result<?> consumeComparedBySale(Integer product_id,
                                           @RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam Integer core_ep_id,
                                           @RequestParam Integer type,
                                           @RequestParam(defaultValue = "false") boolean group,
                                           String sortCol,
                                           String sortType,
                                           @RequestParam(defaultValue = "0") Integer record_start,
                                           @RequestParam(defaultValue = "20") Integer record_count) {
        Date[] dates = Utils.checkDate(start, end);
        if (!Utils.isSortType(sortType)) {
            throw new ApiException("排序方式错误");
        }
        String[] sortCols = new String[]{"before", "after", "fee"};
        if (!ArrayUtils.contains(sortCols, sortCol)) {
            throw new ApiException("排序字段错误");
        }
        int[] types = new int[]{ReportConstant.ConsumeAnalysisType.YEAR_ON_YEAR, ReportConstant.ConsumeAnalysisType.MONTH};
        if (!ArrayUtils.contains(types, type)) {
            throw new ApiException("类型错误");
        }
        return dataAnalysisService.selectConsumeAnalysisComparedSale(product_id, dates[0], dates[1], core_ep_id, type, group ? "team" : "individual", sortCol, sortType, record_start, record_count);
    }
}
