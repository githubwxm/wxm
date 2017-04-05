package com.all580.ep.service;

import com.all580.ep.api.service.EpPushService;
import com.all580.ep.dao.EpPushMapper;
import com.all580.ep.entity.EpPush;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 推送接口服务
 * @date 2017/4/5 15:18
 */
@Service
@Slf4j
public class EpPushServiceImpl implements EpPushService {
    @Autowired
    private EpPushMapper epPushMapper;
    /**
     * 根据企业ID获取推送配置
     * @param epId 企业ID
     * @return
     */
    @Override
    public Result<?> selectByEpId(String epId) {
        List<EpPush> epPushes = epPushMapper.selectByEpId(epId);
        Result<List> result = new Result<>(true);
        result.put(JsonUtils.json2List(JsonUtils.toJson(epPushes)));
        return result;
    }
}
