package com.all580.base.controller.order;

import com.all580.base.util.Utils;
import com.all580.ep.api.conf.EpConstant;
import com.all580.product.api.consts.ProductConstants;
import com.all580.report.api.dto.OrderDto;
import com.all580.report.api.dto.OrderInfo;
import com.all580.report.api.dto.RefundOrderItemDto;
import com.all580.report.api.service.QueryOrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description: 订单查询网关
 * @date 17-5-26 上午9:35
 */
@Controller
@RequestMapping("api/order/query")
@Slf4j
public class QueryOrderController extends BaseController {
    @Autowired
    private QueryOrderService queryOrderService;

    @RequestMapping(value = "line/list")
    @ResponseBody
    public Result<?> listLine(@RequestParam Integer ep_type,
                                          String start_date,
                                          String end_date,
                                          Integer status,
                                          Integer item_status,
                                          String product_name,
                                          String group_number,
                                          String name,
                                          String phone,
                                          Long number,
                                          @RequestParam Integer ep_id,
                                          @RequestParam(defaultValue = "0") Integer record_start,
                                          @RequestParam(defaultValue = "20") Integer record_count) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        Date[] dates = Utils.checkDateTime(start_date, end_date);
        return queryOrderService.queryLineOrderItemList(number, product_name, group_number, status, item_status, name,
                phone, ep_type, coreEpId, ep_id, dates[0], dates[1], record_start, record_count);
    }

    @RequestMapping(value = "line/view")
    @ResponseBody
    public Result<?> viewLine(@RequestParam Integer ep_type, @RequestParam Integer ep_id, @RequestParam Long item_number) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.viewLineOrderItemInfo(item_number, ep_type, coreEpId, ep_id);
    }

    @RequestMapping(value = "line/prerefund")
    @ResponseBody
    public Result<?> preRefund(@RequestParam Integer ep_type, @RequestParam Integer ep_id, @RequestParam Long item_number) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.preRefundLineInfo(item_number, ep_type, coreEpId, ep_id, ProductConstants.RefundEqType.SELLER);
    }

    @RequestMapping(value = "item/list")
    @ResponseBody
    public Result<?> listOrderItems(@RequestParam OrderInfo orderInfo,
                                    @RequestParam(defaultValue = "0") Integer record_start,
                                    @RequestParam(defaultValue = "20") Integer record_count) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        orderInfo.setCoreEpId(coreEpId);

        return queryOrderService.getOrderItemList(orderInfo, record_start, record_count);
    }

    @RequestMapping(value = "item/get_item_detail")
    @ResponseBody
    public Result<OrderDto> getOrderDetailByNumber(@RequestParam("orderSn") Long orderSn,
                                                   @RequestParam("itemSn") Long itemSn,
                                                   @RequestParam("epId")Integer epId,
                                                   @RequestParam("coreEpId")Integer coreEpId) {
        return queryOrderService.getOrderDetailByNumber(orderSn, itemSn, epId, coreEpId);
    }
}
