package com.all580.base.controller.ep;

import com.all580.base.manager.PlatfromValidateManager;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.framework.common.BaseController;
import com.framework.common.Result;

import javax.lang.exception.ParamsMapValidationException;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
@Controller
@RequestMapping("api/ep")
@Slf4j
public class EpController extends BaseController {
    @Autowired
    private EpService epService;

    @Autowired
    private PlatfromValidateManager platfromValidateManager;//select

    /**
     * 创建企业
     * @param map
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map<String,Object>> create(HttpServletRequest request, @RequestBody Map<String,Object> map) {
            map.put(EpConstant.EpKey.CORE_EP_ID,request.getAttribute(EpConstant.EpKey.CORE_EP_ID)) ;
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
            return epService.createEp(map);
    }

    @RequestMapping(value = "updateEpRemoveGroup", method = RequestMethod.POST)
    @ResponseBody
    public Result updateEpRemoveGroup(@RequestBody Map<String,Object> map){
        //creator_ep_id
        return epService.updateEpRemoveGroup(map);
    }
    /**
     * 修改企业
     * @param map
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map<String,Object>> update(@RequestBody Map<String,Object> map) {
        // 验证参数
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
        Integer ep_type=CommonUtil.objectParseInteger(map.get("ep_type"));
        if(!EpConstant.EpType.PLATFORM.equals(ep_type)){
            //  "creator_ep_id", ep_type
            String ep_class = (String) map.get("ep_class");
            if (!CommonUtil.isTrue(ep_class, "\\d+")) {
                throw new ParamsMapValidationException("企业分类错误");
            }
            String creator_ep_id = (String) map.get("creator_ep_id");
            if (!CommonUtil.isTrue(creator_ep_id, "\\d+")) {
                throw new ParamsMapValidationException("创建企业不能为空");
            }
        }
        String id = map.get("id").toString();
        if (!CommonUtil.isTrue(id, "\\d+")) {
            throw new ParamsMapValidationException("企业id错误");
        }
            return epService.updateEp(map);
    }

    /**
     * 冻结企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/freeze", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusFreeze(@RequestBody Map<String,Object> map) {
        // 验证参数
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
           // //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
            return epService.freeze(map);


    }
    /**
     * 停用企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/disable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusDisable(@RequestBody Map<String,Object> map) {
        // 验证参数D
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
            return epService.disable(map);
    }
    /**
     * 激活企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/enable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusEnable(@RequestBody Map<String,Object> map) {
        // 验证参数D
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
            return epService.enable(map);

    }

    /**
     * 查询企业列表
     * @param
     * @return
     */
    @RequestMapping(value = "selectEpList", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map<String,Object>> selectEpList(HttpServletRequest request,String name,String link_phone,
                                        Integer province,Integer city,Integer ep_type,Integer record_start,
                                                   Integer record_count) {
        // 验证参数D
        Map<String,Object> map = new HashMap<>();
        map.put(EpConstant.EpKey.CORE_EP_ID, request.getAttribute(EpConstant.EpKey.CORE_EP_ID));
        map.put(EpConstant.EpKey.NAME,name);
        map.put(EpConstant.EpKey.LINK_PHONE,link_phone);
        map.put(EpConstant.EpKey.PROVINCE,province);
        map.put(EpConstant.EpKey.CITY,city);
        map.put(EpConstant.EpKey.EP_TYPE,ep_type);
        map.put("record_start", record_start);
        map.put("record_count", record_count);

        return epService.select(map);

   }//
    @RequestMapping(value = "updateEpRole", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> updateEpRole(@RequestBody Map<String,Object> map) {
        // 验证参数D  ep_role
        map.put(EpConstant.EpKey.CORE_EP_ID,getAttribute(EpConstant.EpKey.CORE_EP_ID));
        ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
        //map.put(EpConstant.EpKey.ACCESS_KEY,getAttribute(EpConstant.EpKey.ACCESS_KEY));
        return epService.updateEpRole(map);

    }

    /**
     * 检查名字与电话是否存在
     * @param
     * @return
     */
    @RequestMapping(value = "checkNamePhone", method = RequestMethod.GET)
    @ResponseBody
   public Result<Boolean> checkNamePhone(String name,String link_phone,Integer id){
       //where name =#{name} or link_phone=#{link_phone}
       Map<String,Object> map = new HashMap<>();
        map.put("id",id);
       map.put("name",name);
       map.put("link_phone",link_phone);
       return epService.checkNamePhone(map);
   }

    /**
     * 查询下级供应商
     * @param ep_id
     * @return
     */
    @RequestMapping(value = "selectDownSupplier", method = RequestMethod.GET)
    @ResponseBody
   public Result<List<Map<String,Object>>> selectDownSupplier(String ep_id){
       Map<String,Object> map = new HashMap<>();
       map.put("creator_ep_id",ep_id);//查询下级企业id
       map.put("ep_type",EpConstant.EpType.SUPPLIER);
       //ParamsMapValidate.validate(map, platfromValidateManager.generateCreateDownUpValidate());
       return epService.selectDownSupplier(map);
   }
}
