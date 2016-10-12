package com.all580.base.controller.ep;

import com.all580.ep.api.service.CoreEpChannelService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

@Controller
@RequestMapping("ep/platform/channel")
@Slf4j
public class EpChannelController extends BaseController {
    @Autowired
    private CoreEpChannelService coreEpChannelService;


    /**
     *修改通道汇率
     * @param map
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> update(@RequestBody Map map) {

        return coreEpChannelService.update(map);

    }
    /**
     *添加通道汇率
     * @param map
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> create(@RequestBody Map map) {

        return coreEpChannelService.create(map);

    }
    /**
     *取消通道汇率   现在是删除，
     * @param map
     * @return
     */
    @RequestMapping(value = "cancel", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> cancel(@RequestBody Map map) {
        Result<Integer> result = new Result<Integer>();
        Integer id=null;
          try {
              id =  Integer.parseInt(map.get("id").toString());
              result.put(coreEpChannelService.cancel(id).get());
              result.setSuccess();
          }catch (Exception e){
              result.setFail();
              result.setError(Result.DB_FAIL, "数据库修改出错"+e);
          }
        return result;

    }//
    /**
     *添加通道汇率
     * @param map
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> list(@RequestBody Map map) {
        return coreEpChannelService.select(map);

    }

    private Map<String[], ValidRule[]> generateCreateEpChannelValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "supplier_core_ep_id.", // 供应侧平台商ID'
                "seller_core_ep_id", // ''销售侧平台商ID',
                "rate", // 费率
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "supplier_core_ep_id", // 订单子产品ID
                "seller_core_ep_id", // 计划ID
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }

    private Map<String[], ValidRule[]> generateLimitValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id.", //
                "rate", // 费率
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id", // 订单子产品ID
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
