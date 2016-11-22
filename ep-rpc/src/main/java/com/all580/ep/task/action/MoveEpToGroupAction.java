package com.all580.ep.task.action;

import com.all580.product.api.service.PlanGroupRPCService;
import com.framework.common.util.CommonUtil;
import com.github.ltsopensource.core.domain.Action;
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
 * Created by wxming on 2016/11/21 0021.
 */
@Component("EP_TO_GROUP")
public class MoveEpToGroupAction implements JobRunner {
    @Autowired
    private PlanGroupRPCService planGroupService;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result run(JobContext jobContext) throws Throwable {
        //jobContext.getJob().getExtParams();
        Integer epId = CommonUtil.objectParseInteger(jobContext.getJob().getParam("epId"));
        Integer groupId = CommonUtil.objectParseInteger(jobContext.getJob().getParam("groupId"));
        Integer ids = CommonUtil.objectParseInteger(jobContext.getJob().getParam("ids"));
        List<Integer> list = new ArrayList<>();
        list.add(ids);
        com.framework.common.Result r = planGroupService.mvEpsToGroup(epId, groupId, list);
        if (!r.isSuccess()) {
            throw new ApiException("修改企业移动分组失败"+ids);
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }
}
