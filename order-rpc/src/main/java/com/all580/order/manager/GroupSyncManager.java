package com.all580.order.manager;

import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.order.dao.GroupMapper;
import com.all580.order.dao.GroupMemberMapper;
import com.all580.order.dao.GuideMapper;
import com.all580.order.entity.Group;
import com.all580.order.entity.Guide;
import com.framework.common.Result;
import com.framework.common.synchronize.SynchronizeAction;
import com.framework.common.synchronize.SynchronizeDataManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Collections;
import java.util.List;
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
public class GroupSyncManager {

    @Autowired
    private SynchronizeDataManager synchronizeDataManager;

    @Autowired
    private CoreEpAccessService coreEpAccessService;

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
    public Map<String, Object> syncGroup(int groupId) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队数据异常:null");
        }
        return generateSyncByGroup(group)
                // 同步团队表
                .put("t_group", Collections.singletonList(group))
                // 同步
                .sync().getDataMap();
    }

    /**
     * 同步团队删除
     * @param groupId 要删除的团队
     * @return
     */
    public Map<String, Object> syncDeleteGroup(int groupId, Set<Integer> members) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队数据异常:null");
        }
        return generateSyncByGroup(group)
                // 同步团队表
                .delete("t_group", groupId)
                .delete("t_group_member", members)
                // 同步
                .sync().getDataMap();
    }

    /**
     * 同步导游
     * @param guideId 导游ID
     * @return
     */
    public Map<String, Object> syncGuide(int guideId) {
        Guide guide = guideMapper.selectByPrimaryKey(guideId);
        if (guide == null) {
            throw new ApiException("同步导游数据异常:null");
        }
        return generateSyncByGuide(guide)
                // 同步导游表
                .put("t_guide", Collections.singletonList(guide))
                // 同步
                .sync().getDataMap();
    }

    /**
     * 同步导游删除
     * @param guideId 要删除的导游
     * @return
     */
    public Map<String, Object> syncDeleteGuide(int guideId) {
        Guide guide = guideMapper.selectByPrimaryKey(guideId);
        if (guide == null) {
            throw new ApiException("同步导游数据异常:null");
        }
        return generateSyncByGuide(guide)
                // 同步导游表
                .delete("t_guide", guideId)
                // 同步
                .sync().getDataMap();
    }

    /**
     * 同步团队成员
     * @param groupId 团队ID
     * @param lastId 成员最后的ID（增量）
     * @return
     */
    public Map<String, Object> syncAddGroupMember(int groupId, Integer lastId) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队成员数据异常:null");
        }
        return generateSyncByGroup(group)
                // 同步团队成员表
                .put("t_group_member", groupMemberMapper.selectByGroup(groupId, lastId))
                // 同步
                .sync().getDataMap();
    }

    /**
     * 同步团队成员 删除
     * @param groupId 团队ID
     * @param memberId 要删除的成员ID
     * @return
     */
    public Map<String, Object> syncDeleteMember(int groupId, int memberId) {
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("同步团队成员数据异常:null");
        }
        return generateSyncByGroup(group)
                // 同步团队成员表
                .delete("t_group_member", memberId)
                // 同步
                .sync().getDataMap();
    }

    public SynchronizeAction generateSyncByGroup(Group group) {
        return generateSync(group.getCore_ep_id());
    }

    public SynchronizeAction generateSyncByGuide(Guide guide) {
        return generateSync(guide.getCore_ep_id());
    }

    private SynchronizeAction generateSync(Integer coreEpId) {
        if (coreEpId == null) {
            throw new ApiException("sync core ep id is not null.");
        }
        Result<List<String>> accessKeyResult = coreEpAccessService.selectAccessList(Collections.singletonList(coreEpId));
        if (!accessKeyResult.isSuccess()) {
            throw new ApiException(accessKeyResult.getError());
        }
        List<String> accessKeyList = accessKeyResult.get();
        return synchronizeDataManager.generate(accessKeyList.toArray(new String[accessKeyList.size()]));
    }
}
