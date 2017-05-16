package com.all580.base.controller.product;

import com.all580.ep.api.service.EpService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.DistributionEpInfo;
import com.all580.product.api.model.PlanGroupInfo;
import com.all580.product.api.service.PlanGroupRPCService;
import com.all580.product.api.service.ProductDistributionRPCService;
import com.all580.product.api.service.ProductRPCService;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.Paginator;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.*;

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

    @Resource
    EpService epService;

    @Resource
    ProductRPCService productService;

    @Resource
    ProductDistributionRPCService productDistributionService;

    /**
     * 新增商家分组
     * @param params
     * @return
     */
    @RequestMapping(value = "group/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addSaleGroup(@RequestBody Map params) {
        String name = CommonUtil.objectParseString(params.get("name"));
        if(name!=null&&name.length()>10){
            return new Result(false,"分组名字过长");
        }
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
        String name = CommonUtil.objectParseString(params.get("name"));
        if(name!=null&&name.length()>10){
           return new Result(false,"分组名字过长");
        }
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
                CommonUtil.objectParseInteger(params.get("plan_sale_id")),
                CommonUtil.objectParseInteger(params.get("price_type")),
                CommonUtil.objectParseInteger(params.get("price_percent")),
                CommonUtil.objectParseInteger(params.get("price_pixed"))
        );
    }

    @RequestMapping(value = "platform_ep/list")
    @ResponseBody
    public Result<List<Map<String, Object>>> searchPlatformEp(@RequestParam("ep_id") Integer epId, @RequestParam("product_sub_id") Integer productSubId, @RequestParam("is_distributed") Integer isDistributed) {
        return searchIsProviderEp(epId, productSubId, isDistributed);
    }

    private Result<List<Map<String, Object>>> searchIsProviderEp(int epId, int productSubId, int isDistributed) {
        Map param = new HashMap();
        param.put("ep_id", epId);
        Result<Map<String, Object>> epResult = epService.platformListDown(param);
        if (epResult == null || epResult.isFault())
            throw new ApiException("下游平台商查询出错");
        Map<String, Object> map = epResult.get();
        List<Map<String, Object>> eps = (List<Map<String,Object>>) map.get("list");
//        Result<List<DistributionEpInfo>> distributedEpsResult = productSalesPlanService.searchDistributionEpInfo(productSubId, epId);
        Result<List<DistributionEpInfo>> distributedEpsResult = productDistributionService.selectAlreadyDistributionPlatformEp(productSubId, epId);
        if (distributedEpsResult == null) throw new ApiException("查询分销企业出错");
        List<Map<String, Object>> returnList = new ArrayList<>();
        if (ProductConstants.ProductDistributionState.HAD_DISTRIBUTE == isDistributed) {
            for (Map<String, Object> ep : eps) {
                if (distributedEpsResult.get() != null)
                for (DistributionEpInfo distributedEp : distributedEpsResult.get()) {
                    if (((Integer) ep.get("id")).equals(distributedEp.getId())) {
                        ep.put("min_price", distributedEp.getMin_price());       // 最低售价
                        ep.put("buying_price", distributedEp.getBuying_price());    // 进货价
                        ep.put("price_percent", distributedEp.getPrice_percent());   // 加价百分比
                        ep.put("price_pixed", distributedEp.getPrice_pixed());     // 固定加价金额
                        ep.put("price_type", distributedEp.getPrice_type());      // 加价类型
                        returnList.add(ep);
                        break;
                    }
                }
            }
        }
        if (ProductConstants.ProductDistributionState.NOT_DISTRIBUTE == isDistributed) {
            Result<Map> pricesResult = productService.searchPlanSaleAllPrices(epId, productSubId);
             Result<Map> pidsMap= productSalesPlanService.selectByEpIdProductSub(productSubId,epId);
            String pids="";
            if(null!= pidsMap &&pidsMap.get()!=null){//根据pids过滤掉已经ep_id
                pids = CommonUtil.objectParseString(pidsMap.get().get("pids"));
                if(null==pids){
                    pids="";
                }
            }
            String  [] temps= pids.split(",");
            List<Integer> pidsList = new ArrayList<>();
            pidsList.add(epId);
            for(int i=0;i<temps.length;i++){
                Integer pid =CommonUtil.objectParseInteger(temps[0]);
                if( null!=pid){
                    pidsList.add(pid);
                }
            }
            if (pricesResult == null || pricesResult.isFault()) return new Result<>(false, "未查到产品价格信息");
            for (Map<String, Object> ep : eps) {
                // 是否已分销标记
                boolean flag = false;
                if (distributedEpsResult.get() != null)
                for (DistributionEpInfo distributedEp : distributedEpsResult.get()) {
                    if (ep.get("id").equals(distributedEp.getId())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    ep.put("min_price", pricesResult.get().get("min_sell_price"));       // 最低售价
                    ep.put("buying_price", pricesResult.get().get("price"));    // 进货价
                    if(pidsList.contains(ep.get("id"))){
                        continue;
                    }
                    returnList.add(ep);
                }
            }
        }
        Result<List<Map<String, Object>>> result = new Result<>(true);
        result.put(returnList);
        return result;
    }

    @RequestMapping(value = "seller_price/list")
    @ResponseBody
    public Result<List<Map<String, Object>>> searchProductAllPrice(@RequestParam("ep_id") Integer epId, @RequestParam(value = "product_name", required = false) String productName) {
        return productSalesPlanService.searchProductAllPrice(epId, productName);
    }

    @RequestMapping(value = "reatil/price/list")
    @ResponseBody
    public Result<Map<String, Object>> selectProductPrice(Integer ep_id,  String product_name
            , Integer type, Integer province,  Integer city,
                                                          @RequestParam("retail_status") Integer retailStatus     ,
                                                                   Integer record_start,
                                                                   Integer record_count) {
        if(type==null){
            type=ProductConstants.ProductType.SCENERY;
        }
        return productSalesPlanService.selectProductPrice( ep_id,   product_name
                ,  type,  province,   city,
                 retailStatus,
                 record_start,
                 record_count);
    }
    @RequestMapping(value = "seller_price/update/status", method = RequestMethod.POST)
    @ResponseBody
    public Result updateRetailPriceStatus(@RequestBody Map params) {
        Integer status = CommonUtil.objectParseInteger( params.get("status"));
        List<Integer>  list   =(List<Integer>  )params.get("list");
        Assert.notNull(status);
        Assert.notNull(list);
        return productSalesPlanService.updateRetailPriceStatus(status,list);
    }
    @RequestMapping(value = "seller_price/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateSalePriceAndRebateBatch(@RequestBody Map params) {
        //TODO 数据验证
        return productSalesPlanService.updateSalePriceAndRebateBatch(params);
    }

}
