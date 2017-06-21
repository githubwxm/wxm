package com.all580.voucherplatform.task.action;

import com.all580.voucher.api.service.VoucherCallbackService;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Linv2 on 2017-06-21.
 */
@Component("ALL580V3_RETRY_ACTION")
public class All580V3SendMessageAction implements JobRunner {
    @Autowired
    private VoucherCallbackService voucherCallbackService;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Job job = jobContext.getJob();
        String action = job.getParam("action");
        String messageId = job.getParam("messageId");
        String content = job.getParam("content");
        voucherCallbackService.process(action, messageId, content, new Date());
        return new Result(Action.EXECUTE_SUCCESS);
    }
}
