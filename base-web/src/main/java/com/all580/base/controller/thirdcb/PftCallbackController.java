package com.all580.base.controller.thirdcb;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("thirdcb/pft")
public class PftCallbackController {

//    @RequestMapping(value = "notify", method = RequestMethod.POST)
//    @ResponseBody
//    public String notify(@RequestBody Map<String, Object> params) {
//        return "success";
//    }

    @RequestMapping(value="notify", method = RequestMethod.GET)
    public String getNotify() {
        return "success";
    }
}