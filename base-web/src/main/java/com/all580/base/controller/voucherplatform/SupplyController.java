package com.all580.base.controller.voucherplatform;

import com.all580.base.manager.voucherplatform.SupplyValidateManager;
import com.all580.voucherplatform.api.service.SupplyService;
import com.framework.common.Result;
import com.framework.common.validate.ParamsMapValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-26.
 */
@Controller(value = "voucherplatform/supply")
public class SupplyController {
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private SupplyValidateManager supplyValidateManager;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(Map map) {
        ParamsMapValidate.validate(map, supplyValidateManager.createValidate());
        return supplyService.create(map);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(Map map) {
        ParamsMapValidate.validate(map, supplyValidateManager.updateValidate());
        return supplyService.update(map);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Result get(Integer id) {
        return supplyService.getSupply(id);
    }

    @RequestMapping(value = "updateConf", method = RequestMethod.POST)
    @ResponseBody
    public Result updateConf(Map map) {

        ParamsMapValidate.validate(map, supplyValidateManager.updateConfValidate());
        return supplyService.updateConf(map);
    }

    @RequestMapping(value = "updateTicketSys", method = RequestMethod.POST)
    @ResponseBody
    public Result updateTicketSys(Map map) {

        ParamsMapValidate.validate(map, supplyValidateManager.updateTicketSysValidate());
        return supplyService.updateTicketSys(map);
    }


    @RequestMapping(value = "getCount", method = RequestMethod.GET)
    @ResponseBody
    public Result getCount(String name) {
        int count = supplyService.getCount(name);
        Result result = new Result(true);
        result.putExt("data", count);
        return result;
    }

    @RequestMapping(value = "getList", method = RequestMethod.GET)
    @ResponseBody
    public Result getList(String name, Integer recordStart, Integer recordCount) {
        List<Map> mapList = supplyService.getList(name, recordStart, recordCount);
        Result result = new Result(true);
        result.putExt("data", mapList);
        return result;
    }


    @RequestMapping(value = "getProdList", method = RequestMethod.GET)
    @ResponseBody
    public Result getProdList(Integer supplyId) {
        List<Map> mapList = supplyService.getProdList(supplyId);
        Result result = new Result(true);
        result.putExt("data", mapList);
        return result;
    }

    @RequestMapping(value = "getProd", method = RequestMethod.GET)
    @ResponseBody
    public Result getProd(Integer supplyId, String prodId) {
        Map map = supplyService.getProd(supplyId, prodId);
        Result result = new Result(true);
        result.putExt("data", map);
        return result;
    }

    @RequestMapping(value = "setProd", method = RequestMethod.POST)
    @ResponseBody
    public Result setProd(int supplyId, Map map) {
        ParamsMapValidate.validate(map, supplyValidateManager.setProdValidate());
        return supplyService.setProd(supplyId, map);
    }
}
