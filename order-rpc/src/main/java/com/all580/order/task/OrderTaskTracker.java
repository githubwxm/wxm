package com.all580.order.task;

import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobExtInfo;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单任务调度执行器
 * @date 2016/10/10 17:29
 */
@Slf4j
public class OrderTaskTracker implements JobRunner, ApplicationContextAware {
    @Setter
    private ApplicationContext applicationContext;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Job job = jobContext.getJob();
        JobExtInfo jobExtInfo = jobContext.getJobExtInfo();
        String action = job.getParam("$ACTION$");
        log.info("执行任务ID:{},Action:{},Type:{},重试次数:{}", new Object[]{
                job.getTaskId(), action, jobExtInfo.getJobType(), jobExtInfo.getRetryTimes() + 1
        });
        JobRunner actionRunner = applicationContext.getBean(action, JobRunner.class);
        if (actionRunner == null) {
            log.warn("任务ID:{} Action:{} 不存在", job.getTaskId(), action);
            return new Result(Action.EXECUTE_FAILED, "任务执行器不存在");
        }
        try {
            Result result = actionRunner.run(jobContext);
            log.info("任务ID:{} 执行结果:{}", job.getTaskId(), result);
            return result;
        } catch (Throwable e) {
            log.error("任务执行异常", e);
            throw e;
        }
    }
}
