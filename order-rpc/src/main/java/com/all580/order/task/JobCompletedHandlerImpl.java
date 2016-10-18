package com.all580.order.task;

import com.github.ltsopensource.core.domain.JobResult;
import com.github.ltsopensource.jobclient.support.JobCompletedHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 任务完成
 * @date 2016/10/10 19:14
 */
@Slf4j
public class JobCompletedHandlerImpl implements JobCompletedHandler {
    @Override
    public void onComplete(List<JobResult> list) {
        log.info("Job done:{}", list);
    }
}
