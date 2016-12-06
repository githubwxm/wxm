package com.all580.manager;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.dao.CoreEpAccessMapper;
import com.framework.common.synchronize.SynchronizeDataManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/12/2 0002.
 */

@Slf4j
@Component(value = "syncEpData")
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class SyncEpData {

    @Autowired
    private CoreEpAccessMapper coreEpAccessMapper;//ddd

    @Autowired
    private SynchronizeDataManager synchronizeDataManager;
    /**
     * 同步数据
     *
     * @param
     */
    public Map syncEpData(Object coreEpId, String table, List<?> data) {
        try {
            if (!CommonUtil.objectIsNumber(coreEpId)) {
                log.error("同步数据平台商错误 {} {}", table, data);
            }
            if (data.isEmpty()) {
                log.warn("没有要同步的数据");
                return null;
            }
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id", coreEpId);
            List<Map<String, Object>> keyList = coreEpAccessMapper.select(tempMap);
            String key = "";
            if (keyList.isEmpty()) {
                log.error("未找到{} 对应的key", coreEpId);
                throw new ApiException("未找到" + coreEpId + "对应的key");
            } else {
                key = CommonUtil.objectParseString(keyList.get(0).get(EpConstant.EpKey.ACCESS_KEY));
            }
            return synchronizeDataManager.generate(key)
                    .put(table, data)
                    .sync().getDataMapForJsonMap();
        } catch (ApiException e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("同步数据异常");
        }
    }

    public Map syncEpAllData( String table, List<?> data) {
        try {
            if (null==data||data.isEmpty()) {
                log.warn("没有要同步的数据");
                return null;
            }
           List<String> list  = coreEpAccessMapper.selectAll();
           return synchronizeDataManager.generate((String[])list.toArray(new String[list.size()]))
                    .put(table, data)
                    .sync().getDataMapForJsonMap();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("同步数据异常");
        }
    }
    public Map syncEpAllData( String table, Map<String,Object> obj) {
        try {
            if (null==obj||obj.isEmpty()) {
                log.warn("没有要同步的数据");
                return null;
            }
            List<String> list  = coreEpAccessMapper.selectAll();
            List<Map<String,Object>> listData= new ArrayList<>();
            listData.add(obj);
            return synchronizeDataManager.generate((String[])list.toArray(new String[list.size()]))
                    .put(table, listData)
                    .sync().getDataMapForJsonMap();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("同步数据异常");
        }
    }


    public Map<String, Object> syncDeleteAllData( String table, Integer ...ids) {
        try {

            if (null==ids) {
                log.warn("没有要同步的数据");
                return null;
            }
            List<String> list  = coreEpAccessMapper.selectAll();
            Map<String, Object> map=    synchronizeDataManager.generate((String[])list.toArray(new String[list.size()]))
                    .delete(table, ids)
                    .sync().getDataMapForJsonMap();
            return map;
        }  catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("同步数据异常");
        }
    }
}
