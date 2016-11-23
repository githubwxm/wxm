package com.all580.ep.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.notice.api.service.BalanceChangeSubscribeService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.github.ltsopensource.core.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/11/23 0023.
 */
@Service
public class BalanceChangeSubscribeServiceImpl implements BalanceChangeSubscribeService {

    @Autowired
    private EpBalanceThresholdService epBalanceThresholdService;

    @Override
    public Result process(String mnsMsgId, String content, Date createDate) {
        //[{id, epId, }]  epId，coreEpId，balance
        List list = JsonUtils.json2List(content);
        if (list == null) {
            return new Result(true);
        }
        for (Object o : list) {
            Map map = (Map) o;
            Map<String,Object> params = new HashMap<>();
            params.put("id", map.get("epId"));
            params.put(EpConstant.EpKey.CORE_EP_ID, map.get("coreEpId"));
            params.put("balance", map.get("balance"));
            epBalanceThresholdService.warn(params);
        }
        return new Result(true);
    }
}
