package com.all580.base.controller.product;

import com.all580.product.api.model.PlanGroupInfo;
import com.all580.product.api.service.PlanGroupRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 分组，分销接口网关
 */
@Controller
@RequestMapping(value = "api/sale")
public class SaleController extends BaseController {

    @Resource
    PlanGroupRPCService planGroupService;

    @Resource
    ProductSalesPlanRPCService productSalesPlanService;

    /**
     * 新增商家分组
     * @param params
     * @return
     */
    @RequestMapping(value = "group/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addSaleGroup(@RequestBody Map params) {
        //TODO 数据验证
        return planGroupService.addPlanGroup(
                CommonUtil.objectParseString(params.get("name")),
                CommonUtil.objectParseString(params.get("memo")),
                CommonUtil.objectParseInteger(params.get("ep_id"))
        );
    }

    /**
     * 修改分组信息
     * @param params
     * @return
     */
    @RequestMapping(value = "group/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateSaleGroup(@RequestBody Map params) {
        return planGroupService.updatePlanGroup(
                CommonUtil.objectParseInteger(params.get("id")),
                CommonUtil.objectParseString(params.get("name")),
                CommonUtil.objectParseString(params.get("memo")),
                CommonUtil.objectParseInteger(params.get("ep_id"))
        );
    }

    /**
     * 查询分组列表
     * @return
     */
    @RequestMapping(value = "group/list")
    @ResponseBody
    public Result<Paginator<PlanGroupInfo>> searchSalesGroupList(@RequestParam("ep_id") Integer epId, @RequestParam("record_start") Integer start, @RequestParam("record_count") Integer count) {
        //TODO 数据验证
        return planGroupService.searchGroupList(epId, start, count);
    }

    /**
     * 查询组信息
     * @param id
     * @return
     */
    @RequestMapping(value = "group/info")
    @ResponseBody
    public Result<Map> searchSalesGroupInfo(@RequestParam("id") Integer id) {
        return planGroupService.searchPlanGroupById(id);
    }

    /**
     * 添加企业到组
     * @param params
     * @return
     */
    @RequestMapping(value = "group/ep/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addEpsToGroup(@RequestBody Map params) {
        //TODO 数据验证
        return planGroupService.addEpsToGroup(CommonUtil.objectParseInteger(params.get("ep_id")), CommonUtil.objectParseInteger(params.get("id")), (List) params.get("ep_ids"));
    }

    @RequestMapping(value = "group/ep/mv", method = RequestMethod.POST)
    @ResponseBody
    public Result mvEpsToGroup(@RequestBody Map params) {
        //TODO 数据验证
        return planGroupService.addEpsToGroup(CommonUtil.objectParseInteger(params.get("ep_id")), CommonUtil.objectParseInteger(params.get("id")), (List) params.get("ep_ids"));
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProfit(@RequestBody Map params) {
        //TODO 数据验证
        return productSalesPlanService.updatePlanSale(
                CommonUtil.objectParseInteger(params.get("planSaleId")),
                CommonUtil.objectParseInteger(params.get("priceType")),
                CommonUtil.objectParseInteger(params.get("pricePercent")),
                CommonUtil.objectParseInteger(params.get("pricePixed"))
        );
    }
}
