package com.all580.manager;

import com.all580.ep.api.service.CoreEpAccessService;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.BasicSync;
import com.framework.common.synchronize.SyncAccess;
import com.framework.common.synchronize.SynchronizeDataMap;
import com.github.ltsopensource.jobclient.JobClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/6 9:42
 */
@Component
public class BasicSyncDataEvent extends BasicSync implements InitializingBean {


    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;

    @Autowired
    private JobClient jobClient;
    @Value("${task.sync.new.tracker}")
    private String taskTracker;
    @Value("${task.sync.new.maxRetryTimes}")
    private Integer maxRetryTimes;

    @Autowired
    private CoreEpAccessService coreEpAccessService;

    public SyncAccess getAccessKeys(Integer core_ep_id,String key) {
        Map<Integer, String> map = new HashMap<>();
        map.put(core_ep_id,key);
        SyncAccess syncAccess = new SyncAccess();
        syncAccess.setCoreEpId(core_ep_id);
        syncAccess.setAccessKey(key);
        syncAccess.setAccessKeyMap(map);
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[2];
        syncAccess.setDataMap(new SynchronizeDataMap(syncAccess.getAccessKey(), traceElement.getClassName() + "." + traceElement.getMethodName()));
        Assert.notNull(syncAccess.getAccessKey());
        return syncAccess;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setFailJob(jobClient, taskTracker, maxRetryTimes);
        setTopicPushManager(topicPushManager);
        setTopicName(topicName);
    }

    @Override
    protected List<Map<String, Object>> getAccessKeyResult(List<Integer> list) {
        Assert.notEmpty(list);

        Result<List<Map<String, Object>>> accessKeyResult = coreEpAccessService.selectAccessMap(list);
        if (!accessKeyResult.isSuccess()) {
            throw new ApiException(accessKeyResult.getError());
        }
        return accessKeyResult.get();
    }
}

