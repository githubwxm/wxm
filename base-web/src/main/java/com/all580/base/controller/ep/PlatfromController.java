package com.all580.base.controller.ep;

import com.all580.base.manager.PlatfromValidateManager;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.payment.api.service.EpPaymentConfService;
import com.framework.common.BaseController;
import com.framework.common.Result;

import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11 0011.  平台商网关
 */
@Controller
@RequestMapping("api/ep/platform")
@Slf4j
public class PlatfromController extends BaseController {
    @Autowired
    private EpService epService;

    @Autowired
    private PlatfromValidateManager platfromValidateManager;

    @Autowired
    private EpPaymentConfService epPaymentConfService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map<String,Object>> create(@RequestBody Map<String,Object> map) {
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpPlatfromValidate());
        return epService.createPlatform(map);

    }

    @RequestMapping(value = "validate", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> validate( String access_id
    ) {
        Map<String,Object> map = new HashMap<>();
        map.put(EpConstant.EpKey.ACCESS_ID, access_id);
        //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
        return epService.validate(map);

    }


    /**
     *查询平台商id
     */
    @RequestMapping(value = "selectPlatfromId", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> selectPlatfromId(Integer id) {

        return epService.selectId(id);

    }
    /**
     * 平台商停用
     *
     * @param// map
     * @return
     */
    @RequestMapping(value = "status/disable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> disable(@RequestBody Map<String,Object> map) {
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
        return epService.platformDisable(map);


    }

    /**
     * 平台商冻结
     *
     * @param
     * @return
     */
    @RequestMapping(value = "status/freeze", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> freeze(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
        return epService.platformFreeze(map);


    }

    /**
     * 平台商激活
     *
     * @param
     * @return
     */
    @RequestMapping(value = "status/enable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> enable(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
        return epService.platformEnable(map);

    }//

    /**
     * 添加配置支付方式
     *
     * @param
     * @return
     */
    @RequestMapping(value = "payment/add", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> paymentAdd(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreatePaymentValidate());
        map.put("coreEpId",getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return     epPaymentConfService.create(map);
    }//payment

    @RequestMapping(value = "payment/select", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> paymentAdd(@RequestParam(value="id") Integer id) {
        return     epPaymentConfService.selectByPrimaryKey(id);
    }
    /**
     * 修改配置支付方式
     *
     * @param
     * @return
     */
    @RequestMapping(value = "payment/update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> paymentUpdate(@RequestBody Map map) {
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreatePaymentValidate());
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        map.put("coreEpId",getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return     epPaymentConfService.update(map);
    }//payment listByEpId

    /**
     * 支付列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "payment/listByEpId", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String, String>>> paymentListByEpId(Integer ep_id) {
        return     epPaymentConfService.listByEpId(ep_id);
    }//payment

    /**
     * 上游平台商
     *
     * @param
     * @return
     */
    @RequestMapping(value = "list/up", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> platformListUp(
                                       String ep_id,
                                       String epName,
                                      String epProvince,
                                      String epCity,
                                     String epPhone,Integer record_start,Integer record_count) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("ep_id", ep_id);
        map.put("epName", epName);
        map.put("epProvince", epProvince);
        map.put("epCity", epCity);
        map.put("epPhone", epPhone);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateDownUpValidate());
        return epService.platformListUp(map);

    }//list/up

    /**
     * 下游平台商
     *
     * @return
     */
    @RequestMapping(value = "list/down", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> platformListDown( String ep_id,
                                        String epName,
                                        String epProvince,
                                         String epCity,
                                        String epPhone,Integer record_start,Integer record_count) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("ep_id", ep_id);
        map.put("epName", epName);
        map.put("epProvince", epProvince);
        map.put("epCity", epCity);
        map.put("epPhone", epPhone);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateDownUpValidate());


        return epService.platformListDown(map);

    }//list/u

    /**
     * 平台商列表
     *
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> platformList(HttpServletRequest request, Integer record_start,
                                     Integer record_count,
                                     String name,
                                    Integer ep_type,
                                     Integer status,
                                    Integer province,
                                    Integer city,
                                    Integer link_phone) {

        Map<String,Object> map = new HashMap<>();
        map.put("record_start", record_start);
        map.put("record_count", record_count);
        map.put("name", name);
        map.put("ep_type", EpConstant.EpType.PLATFORM);//查询所有平台商
        map.put("status", status);
        map.put("province", province);
        map.put("city", city);
        map.put("link_phone", link_phone);
        return epService.select(map);

    }//list/u

    /**
     * 所有平台上那个下拉框
     * @return
     */
    @RequestMapping(value = "selectPlatform", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> selectPlatform() {
        return epService.selectPlatform();
    }
}
