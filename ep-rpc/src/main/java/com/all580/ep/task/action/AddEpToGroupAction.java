package com.all580.ep.task.action;

import com.all580.product.api.service.PlanGroupRPCService;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxming on 2016/11/24 0024.
 */
@Component("EP_TO_ADD_GROUP")
public class AddEpToGroupAction  implements JobRunner {
    @Autowired
    private PlanGroupRPCService planGroupService;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result run(JobContext jobContext) throws Throwable {
        //jobContext.getJob().getExtParams();
        Job job= jobContext.getJob();
        Integer operator_id = CommonUtil.objectParseInteger(job.getParam("operator_id"));
        Integer epId = CommonUtil.objectParseInteger(job.getParam("epId"));
        String name = CommonUtil.objectParseString(job.getParam("name"));
        Integer group_id = CommonUtil.objectParseInteger(job.getParam("group_id"));
        Integer core_ep_id = CommonUtil.objectParseInteger(job.getParam("core_ep_id"));
        com.framework.common.Result r=  planGroupService.addEpToGroup(operator_id,
                epId,name,core_ep_id,group_id );

        if (!r.isSuccess()) {
            throw new ApiException("添加企业移动分组失败 epId:"+epId+ group_id);
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }
}
