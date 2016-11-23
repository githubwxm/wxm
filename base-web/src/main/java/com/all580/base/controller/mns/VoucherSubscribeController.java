package com.all580.base.controller.mns;

import com.alibaba.fastjson.JSONObject;
import com.all580.base.aop.MnsSubscribeAspect;
import com.all580.voucher.api.service.VoucherCallbackService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 凭证MNS订阅
 * @date 2016/10/19 15:44
 */
@Controller
@RequestMapping("mns/subscribe")
@Slf4j
public class VoucherSubscribeController extends AbstractSubscribeController {
    @Autowired
    private VoucherCallbackService voucherCallbackService;

    @RequestMapping(value = "voucher", method = RequestMethod.POST)
    public void voucher(HttpServletResponse response, ModelMap model) {
        try {
            String id = (String) model.get(MnsSubscribeAspect.MSG_ID);
            String msg = (String) model.get(MnsSubscribeAspect.MSG);
            if (StringUtils.hasText(msg)) {
                Map map = JSONObject.parseObject(msg, Map.class);
                validate(map);
                String action = map.get("action").toString();
                String content = map.get("content").toString();
                Object time = map.get("createTime");
                Date createTime = time == null ? null : DateFormatUtils.converToDateTime(time.toString());
                Result result = voucherCallbackService.process(action, id, content, createTime);
                log.debug("调用凭证回调Action:{}, Result:{}", action, result.toJsonString());
                if (!result.isSuccess()) {
                    throw new Exception(result.getError());
                }
            }
            responseWrite(response, "OK");
        } catch (Exception e) {
            log.error("MNS VOUCHER ERROR", e);
            response.setStatus(500);
        }
    }
}
