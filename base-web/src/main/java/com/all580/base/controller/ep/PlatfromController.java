package com.all580.base.controller.ep;

import com.all580.base.manager.PlatfromValidateManager;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.EpPaymentConfService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map<String,Object>> update(@RequestBody Map<String,Object> map) {
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpPlatfromValidate());
        return epService.updateEp(map);

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
    @RequestMapping(value = "select_type_name", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> selectTypeName(String name,@RequestParam(value="ep_type") Integer ep_type,Integer ep_id) {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("ep_type",ep_type);
        map.put("ep_id",ep_id);
        return epService.selectTypeName(map);

    }

    /**
     *查询平台商id
     */
    @RequestMapping(value = "select_platfrom_id", method = RequestMethod.GET)
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
        //map.put("coreEpId",getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return     epPaymentConfService.create(map);
    }//payment
    @RequestMapping(value = "payment/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> paymentADelete(@RequestBody Map map) {
        map.put("status",0);//如果做假删除就用
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreatePaymentStatusValidate());
        //map.put("coreEpId",getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return     epPaymentConfService.delete(map);
    }

    //平台商收款方式停用
    @RequestMapping(value = "payment/stop", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> paymentStop(@RequestBody Map map) {
        map.put("status",0);
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreatePaymentStatusValidate());
        return     epPaymentConfService.update(map);
    }
    //平台商收款方式启用
    @RequestMapping(value = "payment/start", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> paymentStart(@RequestBody Map map) {
        map.put("status",1);
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreatePaymentStatusValidate());
        return     epPaymentConfService.update(map);
    }
    @RequestMapping(value = "payment/select", method = RequestMethod.GET)
    @ResponseBody
    public Result<Integer> paymentSelect(@RequestParam(value="id") Integer id) {
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
       // map.put("coreEpId",getAttribute(EpConstant.EpKey.CORE_EP_ID));
        return     epPaymentConfService.update(map);
    }

    /**
     * 支付列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "payment/list_by_ep_id", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String, String>>> paymentListByEpId(Integer ep_id) {
        return     epPaymentConfService.listByEpId(ep_id);
    }//payment


    @RequestMapping(value = "payment/list_by_core_ep_id", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<Map<String, String>>> paymentListByEpIdNotApi() {
        Integer core_ep_id=CommonUtil.objectParseInteger(getAttribute(EpConstant.EpKey.CORE_EP_ID));
        Assert.notNull(core_ep_id, "参数【core_ep_id】不能为空");
        Result<List<Map<String, String>>> result =epPaymentConfService.listByEpId(core_ep_id);
        List<Map<String,String>> list = new ArrayList<>();
        for(Map<String, String> map:result.get()){
            Map<String,String> resultMap = new HashMap<>();
            if(PaymentConstant.PaymentType.ALI_PAY-CommonUtil.objectParseInteger(map.get("payment_type"))==0){
                resultMap.put("img",PaymentConstant.PaymentiImg.ALI_PAY_IMG);
                resultMap.put("name","支付宝");
            }else if(PaymentConstant.PaymentType.WX_PAY-CommonUtil.objectParseInteger(map.get("payment_type"))==0){
                resultMap.put("img",PaymentConstant.PaymentiImg.WX_PAY_IMG);
                resultMap.put("name","微信");
            }
            resultMap.put("payment_type",map.remove("payment_type"));
            list.add(resultMap);
        }
        result.put(list);
        return    result ;
    }

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
                                       String name,
                                      String province,
                                      String city,
                                     String link_phone,Integer record_start,Integer record_count) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("ep_id", ep_id);
        map.put("name", name);
        map.put("province", province);
        map.put("city", city);
        map.put("link_phone", link_phone);
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
                                        String name,
                                        String province,
                                         String city,
                                        String link_phone,Integer record_start,Integer record_count) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("ep_id", ep_id);
        map.put("name", name);
        map.put("province", province);
        map.put("city", city);
        map.put("link_phone", link_phone);
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
    @RequestMapping(value = "select_platform", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> selectPlatform(Integer seller_core_ep_id) {
        return epService.selectPlatform(seller_core_ep_id);
    }

    /**
     * 所有平台上那个下拉框
     * @return
     */
    @RequestMapping(value = "update_ep_note_status", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> updateEpNoteStatus(@RequestBody Map<String,Object> params) {
        Integer status = CommonUtil.objectParseInteger(params.get("status"));
        if(null==status){
            Assert.notNull(status, "参数【status】不能为空");
        }
        return epService.updateEpNoteStatus(status);
    }


}
