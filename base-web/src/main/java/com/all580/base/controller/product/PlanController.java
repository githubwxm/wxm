package com.all580.base.controller.product;

import com.framework.common.BaseController;
import com.framework.common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 销售计划接口网关
 */
@Controller
@RequestMapping(value = "api/plan")
public class PlanController extends BaseController {

    /**
     * 增加销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "config")
    @ResponseBody
    public Result<?> addSalesPlan(@RequestBody Map params) {
        return null;
    }

    /**
     * 修改销售计划
     * @param params
     * @return
     */
    @RequestMapping(value = "update")
    @ResponseBody
    public Result<?> updateSalesPlan(@RequestBody Map params) {
        return null;
    }


}
