package com.all580.role.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.com.Common;
import com.all580.manager.SyncEpData;
import com.all580.role.api.service.PlatFuncService;
import com.all580.role.dao.PlatFuncMapper;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxming on 2017/6/29 0029.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class PlatFuncServiceImpl implements PlatFuncService {
    @Autowired
    PlatFuncMapper platFuncMapper;


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SyncEpData syncEpData;
    @Override
    public Result addPlatFunc(int core_ep_id, List<Integer> list) {
        Result result = new Result();
        List<Integer> currentList=  platFuncMapper.selectPlatGroup(core_ep_id);
        List<Integer> deleteList= Common.deleteAllList(list,currentList);//需要删除的
        if(!deleteList.isEmpty()){
            List<Integer> synDeleteList=platFuncMapper.selectPlatGroupList(core_ep_id,deleteList);
            platFuncMapper.deletePlatGroup(core_ep_id,deleteList);
            syncEpData.syncDeleteData(core_ep_id, EpConstant.Table.T_PLAT_FUNC,(Integer [])synDeleteList.toArray(new Integer[synDeleteList.size()]));
        }
        Common.removeAllList(list,currentList);
        if(list != null && !list.isEmpty()){
            platFuncMapper.addPlatFuncList(core_ep_id,list);
            syncEpData.syncEpData(core_ep_id, EpConstant.Table.T_PLAT_FUNC, platFuncMapper.selectPlatGroupListAll(core_ep_id,list));
        }
        Auth.updateAuthMap(null,redisUtils);
        result.setSuccess();
        return result;
    }
}
