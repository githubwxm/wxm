package com.all580.base.controller.report;

import com.all580.base.controller.payment.BalanceController;
import com.all580.base.util.Utils;
import com.all580.ep.api.conf.EpConstant;
import com.all580.report.api.ReportConstant;
import com.all580.report.api.service.DataAnalysisService;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.lang.exception.ApiException;
import java.text.ParseException;
import java.util.Calendar;
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
    public Result<?> consumeListBySale(String product_code,
                                       @RequestParam String start,
                                       @RequestParam String end,
                                       String sort_col,
                                       String sort_type,
                                       @RequestParam(defaultValue = "0") Integer record_start,
                                       @RequestParam(defaultValue = "20") Integer record_count) {
        Date[] dates = checkDate(start, end);
        if (!Utils.isSortType(sort_type)) {
            throw new ApiException("排序方式错误");
        }
        String[] sortCols = new String[]{"individual", "team", "total"};
        if (StringUtils.isNotEmpty(sort_col) && !ArrayUtils.contains(sortCols, sort_col)) {
            throw new ApiException("排序字段错误");
        }
        Object core_ep_id = getAttribute(EpConstant.EpKey.CORE_EP_ID);
        return dataAnalysisService.selectConsumeAnalysisListBySale(product_code, dates[0], dates[1], CommonUtil.objectParseInteger(core_ep_id), sort_col, sort_type, record_start, record_count);
    }

    @RequestMapping("sale/consume/compared")
    @ResponseBody
    public Result<?> consumeComparedBySale(String product_code,
                                           @RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam Integer type,
                                           @RequestParam(defaultValue = "false") boolean group,
                                           String sort_col,
                                           String sort_type,
                                           @RequestParam(defaultValue = "0") Integer record_start,
                                           @RequestParam(defaultValue = "20") Integer record_count) {
        Date[] dates = checkDate(start, end);
        if (!Utils.isSortType(sort_type)) {
            throw new ApiException("排序方式错误");
        }
        String[] sortCols = new String[]{"before", "after", "fee"};
        if (StringUtils.isNotEmpty(sort_col) && !ArrayUtils.contains(sortCols, sort_col)) {
            throw new ApiException("排序字段错误");
        }
        int[] types = new int[]{ReportConstant.ConsumeAnalysisType.YEAR_ON_YEAR, ReportConstant.ConsumeAnalysisType.MONTH};
        if (!ArrayUtils.contains(types, type)) {
            throw new ApiException("类型错误");
        }
        Object core_ep_id = getAttribute(EpConstant.EpKey.CORE_EP_ID);
        return dataAnalysisService.selectConsumeAnalysisComparedSale(product_code, dates[0], dates[1], CommonUtil.objectParseInteger(core_ep_id), type, group ? "team" : "individual", sort_col, sort_type, record_start, record_count);
    }

    @RequestMapping("supply/consume/list")
    @ResponseBody
    public Result<?> consumeListBySupply(String product_code,
                                       @RequestParam String start,
                                       @RequestParam String end,
                                       String sort_col,
                                       String sort_type,
                                       @RequestParam(defaultValue = "0") Integer record_start,
                                       @RequestParam(defaultValue = "20") Integer record_count) {
        Date[] dates = checkDate(start, end);
        if (!Utils.isSortType(sort_type)) {
            throw new ApiException("排序方式错误");
        }
        String[] sortCols = new String[]{"individual", "team", "total"};
        if (StringUtils.isNotEmpty(sort_col) && !ArrayUtils.contains(sortCols, sort_col)) {
            throw new ApiException("排序字段错误");
        }
        Object core_ep_id = getAttribute(EpConstant.EpKey.CORE_EP_ID);
        return dataAnalysisService.selectConsumeAnalysisListBySupply(product_code, dates[0], dates[1], CommonUtil.objectParseInteger(core_ep_id), sort_col, sort_type, record_start, record_count);
    }

    @RequestMapping("supply/consume/compared")
    @ResponseBody
    public Result<?> consumeComparedBySupply(String product_code,
                                           @RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam Integer type,
                                           @RequestParam(defaultValue = "false") boolean group,
                                           String sort_col,
                                           String sort_type,
                                           @RequestParam(defaultValue = "0") Integer record_start,
                                           @RequestParam(defaultValue = "20") Integer record_count) {
        Date[] dates = checkDate(start, end);
        if (!Utils.isSortType(sort_type)) {
            throw new ApiException("排序方式错误");
        }
        String[] sortCols = new String[]{"before", "after", "fee"};
        if (StringUtils.isNotEmpty(sort_col) && !ArrayUtils.contains(sortCols, sort_col)) {
            throw new ApiException("排序字段错误");
        }
        int[] types = new int[]{ReportConstant.ConsumeAnalysisType.YEAR_ON_YEAR, ReportConstant.ConsumeAnalysisType.MONTH};
        if (!ArrayUtils.contains(types, type)) {
            throw new ApiException("类型错误");
        }
        Object core_ep_id = getAttribute(EpConstant.EpKey.CORE_EP_ID);
        return dataAnalysisService.selectConsumeAnalysisComparedSupply(product_code, dates[0], dates[1], CommonUtil.objectParseInteger(core_ep_id), type, group ? "team" : "individual", sort_col, sort_type, record_start, record_count);
    }

    @RequestMapping("search/product")
    @ResponseBody
    public Result<?> searchProduct(String name) {
        Object core_ep_id = getAttribute(EpConstant.EpKey.CORE_EP_ID);
        return dataAnalysisService.searchProduct(name, null, CommonUtil.objectParseInteger(core_ep_id));
    }

    @RequestMapping("search/sub")
    @ResponseBody
    public Result<?> searchSubProduct(String name, String productCode) {
        Object core_ep_id = getAttribute(EpConstant.EpKey.CORE_EP_ID);
        return dataAnalysisService.searchSubProduct(name, productCode, null, CommonUtil.objectParseInteger(core_ep_id));
    }

    public static Date[] checkDate(String start_time, String end_time) {
        Date[] result = new Date[]{null, null};
        try {
            if (start_time != null) {
                String[] split = start_time.split("-");
                if (split.length == 2) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.parseInt(split[0]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(split[1]) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    result[0] = calendar.getTime();
                } else {
                    result[0] = DateFormatUtils.parseString(DateFormatUtils.DATE_FORMAT, start_time);
                }
            }
            if (end_time != null) {
                String[] split = end_time.split("-");
                if (split.length == 2) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.parseInt(split[0]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(split[1]));
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    result[1] = calendar.getTime();
                } else {
                    result[1] = DateFormatUtils.parseString(DateFormatUtils.DATE_FORMAT, end_time);
                }
            }
        } catch (ParseException e) {
            throw new ApiException("日期错误", e);
        }
        return result;
    }
}
