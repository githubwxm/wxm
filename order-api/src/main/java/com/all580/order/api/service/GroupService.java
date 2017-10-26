package com.all580.order.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 团队服务
 * @date 2016/12/1 11:30
 */
public interface GroupService {
    Result<?> addGroup(Map params);

    Result<?> updateGroup(Map params);

    Result<?> delGroup(Map params);

    Result<?> addGuide(Map params);

    Result<?> updateGuide(Map params);

    Result<?> delGuide(Map params);

    Result<?> addGroupMember(Map params);

    Result<?> delGroupMember(Map params);

    Result<?> queryGroupList(Integer core_ep_id,String number,String guide_name,String start,String end,String province,String city,Integer record_start,Integer record_count);

    Result<?> queryGuideList(Integer core_ep_id,String name,String phone,String card,Integer record_start,Integer record_count);

    Result<Map> queryGroupById(Integer id);

    Result<Map> queryGuideById(Integer id);

    Result<?> queryMemberList(Integer group_id,String name,String card,String phone,Integer record_start,Integer record_count);

    Result<?> queryMemberNoPageList(Integer group_id);

    Result<?> queryOrderGuideByOrderId(Integer orderId);

    Result<?> queryOrderMember(Integer suborderid,String name,String card,String phone);
}
