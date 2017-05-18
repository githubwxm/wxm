package com.all580.order.service.event;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.order.dao.OrderItemAccountMapper;
import com.all580.order.entity.Order;
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
    private OrderItemAccountMapper orderItemAccountMapper;

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

    public SyncAccess getAccessKeys(Order order) {
        List<Integer> coreEpIds = orderItemAccountMapper.selectCoreEpIdByOrder(order.getId());
        Map<Integer, String> map = getAccessKeyMap(getAccessKeyResult(coreEpIds));
        SyncAccess syncAccess = new SyncAccess();
        syncAccess.setCoreEpId(order.getPayee_ep_id());
        syncAccess.setAccessKey(map.get(order.getPayee_ep_id()));
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
