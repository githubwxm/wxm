package com.all580.order;

import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/10 17:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/*.xml"})
public class ScheduleTest {

    @Resource
    private JobClient jobClient;

    @Value("${task.tracker}")
    private String tracker;

    @Test
    public void testClient() {
        System.out.println(tracker);
        /*Job job = new Job();
        job.setTaskId("3213213123-1");
        job.setParam("shopId", "11111");
        job.setTaskTrackerNodeGroup("order_TaskTracker");
        job.setMaxRetryTimes(3);
        job.setNeedFeedback(true);
        job.setRepeatInterval(1000L);
        jobClient.submitJob(job);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
