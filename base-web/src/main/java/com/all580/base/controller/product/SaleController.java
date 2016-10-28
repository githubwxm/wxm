package com.all580.base.controller.product;

import com.all580.product.api.service.PlanGroupRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /**
     * 新增商家分组
     * @param params
     * @return
     */
    @RequestMapping(value = "group/add")
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
    @RequestMapping(value = "group/update")
    @ResponseBody
    public Result<?> updateSaleGroup(@RequestBody Map params) {
        return null;
    }

    /**
     * 查询分组列表
     * @return
     */
    @RequestMapping(value = "group/list")
    @ResponseBody
    public Result<?> searchSalesGroupList(@RequestParam("ep_id") Integer epId,  @RequestParam("record_start") Integer start, @RequestParam("count") Integer count) {
        //TODO 数据验证
        return planGroupService.searchGroupList(epId, start, count);
    }

    /**
     * 添加企业到组
     * @param params
     * @return
     */
    @RequestMapping("group/ep/add")
    @ResponseBody
    public Result addEpsToGroup(@RequestBody Map params) {
        //TODO 数据验证
        return planGroupService.addEpsToGroup(CommonUtil.objectParseInteger(params.get("ep_id")), CommonUtil.objectParseInteger(params.get("id")), (List) params.get("ep_ids"));
    }

}
