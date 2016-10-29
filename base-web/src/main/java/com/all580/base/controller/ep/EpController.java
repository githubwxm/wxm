package com.all580.base.controller.ep;

import com.all580.base.manager.PlatfromValidateManager;
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
    public Result<Map> create(HttpServletRequest request, @RequestBody Map<String,Object> map) {
            map.put("core_ep_id",request.getAttribute("core_ep_id")) ;
            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
            return epService.createEp(map);
    }


    /**
     * 修改企业
     * @param map
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> update(@RequestBody Map map) {
        // 验证参数

            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateEpValidate());
            String ep_class = (String) map.get("ep_class");
            String id = map.get("id").toString();
            if (!CommonUtil.isTrue(id, "\\d+")) {
                throw new ParamsMapValidationException("企业id错误");
            }
            if (!CommonUtil.isTrue(ep_class, "\\d+")) {
                throw new ParamsMapValidationException("企业分类错误");
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
    public Result<Integer> statusFreeze(@RequestBody Map map) {
        // 验证参数

            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
            return epService.freeze(map);


    }
    /**
     * 停用企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/disable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusDisable(@RequestBody Map map) {
        // 验证参数D

            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
            return epService.disable(map);
    }
    /**
     * 激活企业
     * @param map
     * @return
     */
    @RequestMapping(value = "status/enable", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> statusEnable(@RequestBody Map map) {
        // 验证参数D

            ParamsMapValidate.validate(map, platfromValidateManager.generateCreateStatusValidate());
            return epService.enable(map);

    }
}
