package com.all580.base.controller.report;

import com.all580.ep.api.conf.EpConstant;
import com.all580.report.api.service.ReportExportTaskService;
import com.framework.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.text.html.CSS.getAttribute;

/**
 * Created by wxming on 2017/8/21 0021.
 */
@Controller
@RequestMapping("api/report/export")
public class ReportExportTaskController {

    @Autowired
    private ReportExportTaskService reportExportTaskService;


    @RequestMapping(value = "task/list", method = RequestMethod.GET)
    @ResponseBody
    public Result selectExportTask(Integer ep_id,
                                            String start  , String end,
                                             String export_name,
                                            Integer record_start, Integer record_count){
        Map<String,Object> map = new HashMap<>();
        map.put( EpConstant.EpKey.CORE_EP_ID,getAttribute( EpConstant.EpKey.CORE_EP_ID));
        map.put("ep_id",ep_id);
        map.put("start",start);
        map.put("end",end);
        map.put("export_name",export_name);
        map.put("record_start",record_start);
        map.put("record_count",record_count);
        return  reportExportTaskService.selectExportTask(map);
    }


}
