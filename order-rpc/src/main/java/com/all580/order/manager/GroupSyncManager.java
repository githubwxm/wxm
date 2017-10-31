package com.all580.order.manager;

import com.all580.order.dao.GroupMapper;
import com.all580.order.dao.GroupMemberMapper;
import com.all580.order.dao.GuideMapper;
import com.all580.order.entity.Group;
import com.all580.order.entity.Guide;
import com.all580.order.service.event.BasicSyncDataEvent;
import com.framework.common.synchronize.SyncAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Map;
import java.util.Set;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/12/1 14:50
 */
@Component
@Slf4j
public class GroupSyncManager extends BasicSyncDataEvent {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GuideMapper guideMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;

    /**
     * 同步团队
     * @param groupId 团队ID
     * @return
     */
    public Map syncGroup(int groupId, Integer guideId) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队数据异常:null");
        }
        SyncAccess syncAccess = getAccessKeys(group.getCore_ep_id());
        syncAccess.getDataMap().add("t_group", group);
        if (guideId != null) {
            syncAccess.getDataMap().add("t_guide", guideMapper.selectByPrimaryKey(guideId));
        }
        Map data = syncAccess.getDataMap().asMap();
        sync(syncAccess.getDataMaps());
        return data;
    }

    /**
     * 同步团队
     * @param groupId 团队ID
     * @return
     */
    public Map syncGroup(int groupId) {
        return syncGroup(groupId, null);
    }

    /**
     * 同步团队删除
     * @param groupId 要删除的团队
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map syncDeleteGroup(int groupId, Set<Integer> members) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队数据异常:null");
        }
        int ret = groupMapper.deleteByPrimaryKey(groupId);
        if (ret <= 0) {
            throw new ApiException("同步团队数据异常:删除团队失败");
        }
        SyncAccess syncAccess = getAccessKeys(group.getCore_ep_id());
        syncAccess.getDataMap()
                .delete("t_group", groupId)
                .delete("t_group_member", members);
        Map data = syncAccess.getDataMap().asMap();
        sync(syncAccess.getDataMaps());
        return data;
    }

    /**
     * 同步导游
     * @param guideId 导游ID
     * @return
     */
    public Map syncGuide(int guideId) {
        Guide guide = guideMapper.selectByPrimaryKey(guideId);
        if (guide == null) {
            throw new ApiException("同步导游数据异常:null");
        }
        SyncAccess syncAccess = getAccessKeys(guide.getCore_ep_id());
        syncAccess.getDataMap().add("t_guide", guide);
        Map data = syncAccess.getDataMap().asMap();
        sync(syncAccess.getDataMaps());
        return data;
    }

    /**
     * 同步导游删除
     * @param guideId 要删除的导游
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map syncDeleteGuide(int guideId) {
        Guide guide = guideMapper.selectByPrimaryKey(guideId);
        if (guide == null) {
            throw new ApiException("同步导游数据异常:null");
        }
        int ret = guideMapper.deleteByPrimaryKey(guideId);
        if (ret <= 0) {
            throw new ApiException("同步导游数据异常:删除导游失败");
        }
        SyncAccess syncAccess = getAccessKeys(guide.getCore_ep_id());
        syncAccess.getDataMap()
                .delete("t_guide", guideId);
        Map data = syncAccess.getDataMap().asMap();
        sync(syncAccess.getDataMaps());
        return data;
    }

    /**
     * 同步团队成员
     * @param groupId 团队ID
     * @param lastId 成员最后的ID（增量）
     * @return
     */
    public Map syncAddGroupMember(int groupId, Integer lastId) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队成员数据异常:null");
        }
        SyncAccess syncAccess = getAccessKeys(group.getCore_ep_id());
        syncAccess.getDataMap().add("t_group_member", groupMemberMapper.selectByGroup(groupId, lastId));
        Map data = syncAccess.getDataMap().asMap();
        //sync(syncAccess.getDataMaps());
        return data;
    }

    /**
     * 同步团队成员 删除
     * @param groupId 团队ID
     * @param memberId 要删除的成员ID
     * @return
     */
    public Map syncDeleteMember(int groupId, int memberId) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队成员数据异常:null");
        }
        SyncAccess syncAccess = getAccessKeys(group.getCore_ep_id());
        syncAccess.getDataMap()
                .delete("t_group_member", memberId);
        Map data = syncAccess.getDataMap().asMap();
        sync(syncAccess.getDataMaps());
        return data;
    }
}
