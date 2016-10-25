package com.all580.base.controller.product;

import com.framework.common.BaseController;
import com.framework.common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 分组，分销接口网关
 */
@Controller
@RequestMapping(value = "api/sale")
public class SaleController extends BaseController {

    /**
     * 新增商家分组
     * @param params
     * @return
     */
    @RequestMapping(value = "group/add")
    @ResponseBody
    public Result<?> addSaleGroup(@RequestBody Map params) {
        return null;
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
    public Result<?> searchSalesGroupList(@RequestParam Integer epId, @RequestParam Integer productSubId, @RequestParam Integer recordStart, @RequestParam Integer recordCount) {
        return null;
    }



}
