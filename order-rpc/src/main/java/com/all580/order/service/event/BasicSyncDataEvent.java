package com.all580.order.service.event;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.order.dao.OrderItemAccountMapper;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.framework.common.Result;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.synchronize.SynchronizeDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.Collection;
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
public class BasicSyncDataEvent {
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;

    @Autowired
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;

    @Autowired
    private CoreEpAccessService coreEpAccessService;

    public SyncAccess getAccessKeys(Order order) {
        List<Integer> coreEpIds = orderItemAccountMapper.selectCoreEpIdByOrder(order.getId());
        Assert.notEmpty(coreEpIds);

        Result<List<Map<String, Object>>> accessKeyResult = coreEpAccessService.selectAccessMap(coreEpIds);
        if (!accessKeyResult.isSuccess()) {
            throw new ApiException(accessKeyResult.getError());
        }
        List<Map<String, Object>> accessKeyList = accessKeyResult.get();
        Assert.notEmpty(accessKeyList);
        Map<Integer, String> map = new HashMap<>();
        for (Map<String, Object> objectMap : accessKeyList) {
            Object id = objectMap.get("id");
            Object access_key = objectMap.get("access_key");
            Assert.noNullElements(new Object[]{id, access_key});
            map.put(Integer.parseInt(id.toString()), access_key.toString());
        }
        SyncAccess syncAccess = new SyncAccess();
        syncAccess.setOrder(order);
        syncAccess.setCoreEpId(order.getPayee_ep_id());
        syncAccess.setAccessKey(map.get(order.getPayee_ep_id()));
        syncAccess.setAccessKeyMap(map);
        syncAccess.setDataMap(new SynchronizeDataMap(syncAccess.getAccessKey()));
        Assert.notNull(syncAccess.getAccessKey());
        return syncAccess;
    }

    public void sync(Collection<SynchronizeDataMap> dataMaps) {
        Assert.notEmpty(dataMaps);
        for (SynchronizeDataMap dataMap : dataMaps) {
            try {
                dataMap.sendToMns(topicPushManager, topicName).clear();
            } catch (Throwable e) {
                throw new ApiException("同步数据异常: " + dataMap.getKey(), e);
            }
        }
    }
}
