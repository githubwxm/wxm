package com.all580.base.controller.index;

import com.all580.report.api.service.NoticeService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxming on 2017/11/1 0001.
 */
@Controller
@RequestMapping("api/notice")
@Slf4j
public class NoticeController extends BaseController {
    @Autowired
    NoticeService noticeService;


    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Map<String,Object> params) {
        return   noticeService.addNotice(params);
    }
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteNotice(@RequestBody Map<String,Object> params) {
        Integer id = CommonUtil.objectParseInteger(params.get("id")) ;
        Assert.notNull(id, "id不能为空");
        return   noticeService.deleteNotice(id);
    }
    @RequestMapping(value = "push", method = RequestMethod.POST)
    @ResponseBody
    public Result upNotice(@RequestBody Map<String,Object> params) {
        Integer id = CommonUtil.objectParseInteger(params.get("id")) ;
        Assert.notNull(id, "id不能为空");
        return   noticeService.upNotice(id);
    }
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map<String,Object> params) {
      return   noticeService.updateNotice(params);
    }
    @RequestMapping(value = "infos", method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> selectId(@RequestParam("id") Integer id){
        return noticeService.selectNoticeId(id);
    }
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Result selectList(Integer title, Integer content,Integer status, Integer record_start,
                                                   Integer record_count){
        Map map = new HashMap();
        if(status==null){
            status=2;
        }
        map.put("status",status);
        map.put("title",title);
        map.put("content",content);
        map.put(record_start,record_start);
        map.put(record_count,record_count);
        return noticeService.selectNotice(map);
    }
}
