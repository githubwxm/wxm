package com.all580.order.dto;

import com.all580.order.entity.Order;
import com.framework.common.synchronize.SynchronizeDataMap;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/6 10:43
 */
@Data
public class SyncAccess {
    private Order order;
    private String accessKey;
    private Integer coreEpId;
    private Map<Integer, String> accessKeyMap;
    private SynchronizeDataMap dataMap;
    private Set<SynchronizeDataMap> dataMaps;
    public SyncAccess addDataMap(SynchronizeDataMap dataMap) {
        if (this.dataMaps == null) this.dataMaps = new HashSet<>();
        this.dataMaps.add(dataMap);
        return this;
    }
    public void setDataMap(SynchronizeDataMap dataMap) {
        this.dataMap = dataMap;
        addDataMap(dataMap);
    }

    public SynchronizeDataMap copy(Integer coreEpId) {
        Assert.notNull(this.dataMap);
        String key = this.accessKeyMap.get(coreEpId);
        Assert.notNull(key);
        return this.dataMap.copy(key);
    }

    public void loop() {
        for (String key : this.accessKeyMap.values()) {
            if (!key.equals(this.accessKey)) {
                this.addDataMap(this.dataMap.copy(key));
            }
        }
    }
}
