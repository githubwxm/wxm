package com.all580.order.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.GroupService;
import com.all580.order.dao.*;
import com.all580.order.entity.Group;
import com.all580.order.entity.GroupMember;
import com.all580.order.entity.Guide;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.GroupSyncManager;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.CheckIdCardUtils;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 团队服务
 * @date 2016/12/1 14:29
 */
@Service
@Slf4j
public class GroupServiceImpl implements GroupService {
    private String dateFormat = "yyyy-MM-dd";
    @Autowired
    private GroupSyncManager groupSyncManager;

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GuideMapper guideMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> addGroup(Map params) {
        Group group = JsonUtils.map2obj(params, Group.class, dateFormat);
        group.setId(null);
        group.setCore_ep_id(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        group.setEp_id(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)));
        group.setCreate_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        group.setCreate_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        group.setCreate_time(new Date());
        int ret = groupMapper.insertSelective(group);
        if (ret <= 0) {
            throw new ApiException("新增团队失败");
        }
        Integer guideId = CommonUtil.objectParseInteger(params.get("guide_id"));
        return addOrUpdateGroup(group, guideId);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> updateGroup(Map params) {
        int groupId = CommonUtil.objectParseInteger(params.get("group_id"));
        // 检查团队操作权限
        checkGroupOperation(groupId, true, CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)),
                CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        Group group = JsonUtils.map2obj(params, Group.class, dateFormat);
        group.setId(groupId);
        group.setCore_ep_id(null);
        group.setEp_id(null);
        group.setCreate_user_id(null);
        group.setCreate_user_name(null);
        group.setCreate_time(null);
        int ret = groupMapper.updateByPrimaryKeySelective(group);
        if (ret <= 0) {
            throw new ApiException("修改团队失败");
        }
        Integer guideId = CommonUtil.objectParseInteger(params.get("guide_id"));
        return addOrUpdateGroup(group, guideId);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> delGroup(Map params) {
        int groupId = CommonUtil.objectParseInteger(params.get("group_id"));
        // 检查团队操作权限
        checkGroupOperation(groupId, true, CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)),
                CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        Set<Integer> members = groupMemberMapper.selectIdsByGroup(groupId);
        int ret = groupMemberMapper.deleteByGroup(groupId);
        if (members != null && ret != members.size()) {
            throw new ApiException("删除团队失败");
        }
        return new Result<>(true).putExt(Result.SYNC_DATA, groupSyncManager.syncDeleteGroup(groupId, members));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> addGuide(Map params) {
        Guide guide = JsonUtils.map2obj(params, Guide.class);
        guide.setId(null);
        guide.setCore_ep_id(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        guide.setEp_id(CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)));
        guide.setCreate_user_id(CommonUtil.objectParseInteger(params.get("operator_id")));
        guide.setCreate_user_name(CommonUtil.objectParseString(params.get("operator_name")));
        guide.setCreate_time(new Date());
        guideMapper.insertSelective(guide);
        return new Result<>(true).putExt(Result.SYNC_DATA, groupSyncManager.syncGuide(guide.getId()));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> updateGuide(Map params) {
        int guideId = CommonUtil.objectParseInteger(params.get("guide_id"));
        // 检查导游操作权限
        checkGuideOperation(guideId, CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)),
                CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        Guide guide = JsonUtils.map2obj(params, Guide.class);
        guide.setId(guideId);
        guide.setCore_ep_id(null);
        guide.setEp_id(null);
        guide.setCreate_user_id(null);
        guide.setCreate_user_name(null);
        guide.setCreate_time(null);
        int ret = guideMapper.updateByPrimaryKeySelective(guide);
        return new Result<>(ret > 0).putExt(Result.SYNC_DATA, groupSyncManager.syncGuide(guide.getId()));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> delGuide(Map params) {
        int guideId = CommonUtil.objectParseInteger(params.get("guide_id"));
        // 检查导游操作权限
        checkGuideOperation(guideId, CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)),
                CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        return new Result<>(true).putExt(Result.SYNC_DATA, groupSyncManager.syncDeleteGuide(guideId));
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> addGroupMember(Map params) {
        int groupId = CommonUtil.objectParseInteger(params.get("group_id"));
        // 检查团队操作权限
        checkGroupOperation(groupId, CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)),
                CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        List memberList = (List) params.get("member");
        if (memberList != null) {
            Integer lastId = null;
            for (Object o : memberList) {
                Map memberMap = (Map) o;
                GroupMember member = JsonUtils.map2obj(memberMap, GroupMember.class);
                if (member.getCard_type() == OrderConstant.CardType.ID) {
                    if (!CheckIdCardUtils.validateCard(member.getCard())) {
                        throw new ApiException(String.format("游客:%s 证件为身份证:%s 格式错误", member.getName(), member.getCard()));
                    }
                }
                member.setGroup_id(groupId);
                int ret = groupMemberMapper.insertSelective(member);
                if (ret <= 0) {
                    throw new ApiException("保存团队成员失败:" + JsonUtils.toJson(o));
                }
                if (lastId == null) {
                    lastId = member.getId();
                }
            }
            return new Result<>(true).putExt(Result.SYNC_DATA, groupSyncManager.syncAddGroupMember(groupId, lastId));
        }
        return new Result<>(true);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> delGroupMember(Map params) {
        int groupId = CommonUtil.objectParseInteger(params.get("group_id"));
        // 检查团队操作权限
        checkGroupOperation(groupId, CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID)),
                CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID)));
        // 检查是否下过单
        int memberId = CommonUtil.objectParseInteger(params.get("member_id"));
        GroupMember member = groupMemberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            throw new ApiException("团队成员不存在");
        }
        Visitor visitor = visitorMapper.selectBySidAndGroup(member.getCard(), groupId);
        if (visitor != null) {
            throw new ApiException("该成员已下单,不允许删除");
        }
        int ret = groupMemberMapper.deleteByPrimaryKey(memberId);
        return new Result<>(ret > 0).putExt(Result.SYNC_DATA, groupSyncManager.syncDeleteMember(groupId, memberId));
    }

    private void checkGroupOperation(int groupId, boolean isOrder, Integer epId, Integer coreEpId) {
        Group old = groupMapper.selectByPrimaryKey(groupId);
        if (old == null) {
            throw new ApiException("团队不存在");
        }
        if (old.getEp_id().intValue() != epId ||
                old.getCore_ep_id().intValue() != coreEpId) {
            throw new ApiException("非法请求:当前企业没有该团队操作权限");
        }
        if (isOrder) {
            int count = orderItemMapper.selectCountByGroup(groupId);
            if (count > 0) {
                throw new ApiException("当前团队已下单");
            }
        }
    }

    private void checkGroupOperation(int groupId, Integer epId, Integer coreEpId) {
        checkGroupOperation(groupId, false, epId, coreEpId);
    }

    private void checkGuideOperation(int guideId, Integer epId, Integer coreEpId) {
        Guide old = guideMapper.selectByPrimaryKey(guideId);
        if (old == null) {
            throw new ApiException("导游不存在");
        }
        if (old.getEp_id().intValue() != epId ||
                old.getCore_ep_id().intValue() != coreEpId) {
            throw new ApiException("非法请求:当前企业没有该导游操作权限");
        }
    }

    private Result<?> addOrUpdateGroup(Group group, Integer guideId) {
        if (guideId != null) {
            Guide guide = guideMapper.selectByPrimaryKey(guideId);
            if (guide == null) {
                throw new ApiException("导游不存在");
            }
            return new Result<>(true).putExt(Result.SYNC_DATA, groupSyncManager.syncGroup(group.getId()));
        }
        Guide guide = new Guide();
        guide.setCore_ep_id(group.getCore_ep_id());
        guide.setEp_id(group.getEp_id());
        guide.setCreate_user_id(group.getCreate_user_id());
        guide.setCreate_user_name(group.getCreate_user_name());
        guide.setCreate_time(group.getCreate_time());

        guide.setName(group.getGuide_name());
        guide.setPhone(group.getGuide_phone());
        guide.setSid(group.getGuide_sid());
        guide.setCard(group.getGuide_card());
        int ret = guideMapper.insertSelective(guide);
        if (ret <= 0) {
            throw new ApiException("新增导游失败");
        }
        return new Result<>(true).putExt(Result.SYNC_DATA, groupSyncManager.syncGroup(group.getId(), guide.getId()));
    }
}
