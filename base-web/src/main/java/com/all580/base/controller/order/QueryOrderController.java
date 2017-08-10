package com.all580.base.controller.order;

import com.all580.base.manager.GernerateValidate;
import com.all580.base.util.Utils;
import com.all580.ep.api.conf.EpConstant;
import com.all580.product.api.consts.ProductConstants;
import com.all580.report.api.dto.*;
import com.all580.report.api.service.QueryOrderService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ValidRule;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
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
                                          Integer[] status,
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
        return queryOrderService.queryLineOrderItemList(number, product_name, group_number, status, name,
                phone, ep_type, coreEpId, ep_id, dates[0], dates[1], record_start, record_count);
    }

    @RequestMapping(value = "line/view")
    @ResponseBody
    public Result<?> viewLine(@RequestParam(required = false) Integer ep_type, @RequestParam Integer ep_id, @RequestParam Long item_number) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.viewLineOrderItemInfo(item_number, ep_type, coreEpId, ep_id);
    }

    @RequestMapping(value = "line/prerefund")
    @ResponseBody
    public Result<?> preRefund(@RequestParam Integer ep_type, @RequestParam Integer ep_id, @RequestParam Long item_number) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.preRefundLineInfo(item_number, ep_type, coreEpId, ep_id, ProductConstants.RefundEqType.SELLER);
    }

    @RequestMapping(value = "item/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<OrderItemDto>> listOrderItems(OrderInfo orderInfo,
                                                           @RequestParam(defaultValue = "0") Integer record_start,
                                                           @RequestParam(defaultValue = "20") Integer record_count) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        orderInfo.setCore_ep_id(coreEpId);
        GernerateValidate validate = new GernerateValidate();
        validate.addRules(new String[]{"productType","ep_type","ep_id","core_ep_id"},new ValidRule[]{new ValidRule.NotNull()})
                .validate(orderInfo);
       return queryOrderService.getOrderItemList(orderInfo, record_start, record_count);
    }

    @RequestMapping(value = "item/get_item_detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<OrderItemDetailDto> getOrderDetailByNumber(@RequestParam("itemSn") Long itemSn,
                                                             @RequestParam(value = "show_accout", required = false, defaultValue = "0") Integer showAccount,
                                                             @RequestParam(value = "ep_type", required = false) Integer epType,
                                                             @RequestParam("ep_id") Integer epId) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.getOrderDetailByNumber(itemSn, epType, coreEpId, epId, showAccount);
    }

    @RequestMapping(value = "scenery/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<SceneryOrder>> listSceneryOrderItems(OrderInfo orderInfo,
                                                                  @RequestParam(defaultValue = "0") Integer record_start,
                                                                  @RequestParam(defaultValue = "20") Integer record_count) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        orderInfo.setCore_ep_id(coreEpId);
        GernerateValidate validate = new GernerateValidate();
        validate.addRules(new String[]{"ep_type","ep_id","core_ep_id"},new ValidRule[]{new ValidRule.NotNull()})
                .validate(orderInfo);
        return queryOrderService.getSceneryOrderItemList(orderInfo, record_start, record_count);
    }

    @RequestMapping(value = "scenery/get_item_detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<SceneryOrderDetail> getSceneryOrderDetailByNumber(@RequestParam("itemSn") Long itemSn,
                                                             @RequestParam(value = "show_accout", required = false, defaultValue = "0") Integer showAccount,
                                                             @RequestParam(value = "ep_type", required = false) Integer epType,
                                                             @RequestParam("ep_id") Integer epId) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.getSceneryOrderDetailByNumber(itemSn, epType, coreEpId, epId, showAccount);
    }

    @RequestMapping(value = "hotel/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<HotelOrder>> listHotelOrderItems(OrderInfo orderInfo,
                                                                  @RequestParam(defaultValue = "0") Integer record_start,
                                                                  @RequestParam(defaultValue = "20") Integer record_count) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        orderInfo.setCore_ep_id(coreEpId);
        GernerateValidate validate = new GernerateValidate();
        validate.addRules(new String[]{"ep_type","ep_id","core_ep_id"},new ValidRule[]{new ValidRule.NotNull()})
                .validate(orderInfo);
        return queryOrderService.getHotelOrderItemList(orderInfo, record_start, record_count);
    }

    @RequestMapping(value = "hotel/get_item_detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<HotelOrderDetail> getHotelOrderDetailByNumber(@RequestParam("itemSn") Long itemSn,
                                                                    @RequestParam(value = "show_accout", required = false, defaultValue = "0") Integer showAccount,
                                                                    @RequestParam(value = "ep_type", required = false) Integer epType,
                                                                    @RequestParam("ep_id") Integer epId) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.getHotelOrderDetailByNumber(itemSn, epType, coreEpId, epId, showAccount);
    }

    @RequestMapping(value = "item/pre_refund", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> preRefundOrder(@RequestParam("itemSn") Long itemSn,
                                      @RequestParam(value = "ep_type", required = false) Integer epType,
                                      @RequestParam("ep_id") Integer epId) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.preRefundOrderInfo(itemSn, epType, coreEpId, epId, ProductConstants.RefundEqType.SELLER);
    }

    @RequestMapping(value = "item/refund_audit", method = RequestMethod.GET)
    @ResponseBody
    public Result<RefundAuditInfo> getRefundOrderAuditInfo(@RequestParam("itemSn") Long itemSn,
                                                  @RequestParam(value = "ep_type", required = false) Integer epType,
                                                  @RequestParam("ep_id") Integer epId) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.getRefundOrderAuditInfo(itemSn, epType, coreEpId, epId);
    }

    @RequestMapping(value = "hotel/pre_clearance", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> preClearanceHotelInfo(@RequestParam("itemSn") Long itemSn) {
        return queryOrderService.preClearanceHotelInfo(itemSn);
    }

    @RequestMapping(value = "visitor/get_list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<VisitorDto>> getVisitorsByOrderItemNumber(@RequestParam("itemSn") Long itemSn) {
        return queryOrderService.getVisitorsByOrderItemNumber(itemSn);
    }

    @RequestMapping(value = "package/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<PackageOrder>> listPackageOrderItem(OrderInfo orderInfo,
                                                                 @RequestParam(defaultValue = "0") Integer record_start,
                                                                 @RequestParam(defaultValue = "20") Integer record_count) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        orderInfo.setCore_ep_id(coreEpId);
        return queryOrderService.getPackageOrderItemList(orderInfo, record_start, record_count);
    }

    @RequestMapping(value = "package/get_item_detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<PackageOrderDetail> getPackageOrderDetailByNumber(@RequestParam("itemSn") Long itemSn,
                                                                @RequestParam(value = "show_accout", required = false, defaultValue = "0") Integer showAccount,
                                                                @RequestParam(value = "ep_type", required = false) Integer epType,
                                                                @RequestParam("ep_id") Integer epId) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.getPackageOrderDetailByNumber(itemSn, epType, coreEpId, epId, showAccount);
    }

    @RequestMapping(value = "package/pre_refund", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> getRefundPackageAuditInfo(@RequestParam("itemSn") Long itemSn,
                                                           @RequestParam(value = "ep_type", required = false) Integer epType,
                                                           @RequestParam("ep_id") Integer epId) {
        Integer coreEpId = CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return queryOrderService.preRefundPackageInfo(itemSn, epType, coreEpId, epId, ProductConstants.RefundEqType.SELLER);
    }
}
