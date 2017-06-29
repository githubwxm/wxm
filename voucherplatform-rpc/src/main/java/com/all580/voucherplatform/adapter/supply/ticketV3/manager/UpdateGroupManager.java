package com.all580.voucherplatform.adapter.supply.ticketV3.manager;

import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.GroupVisitorMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.GroupVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */

@Service
@Slf4j
public class UpdateGroupManager {
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    private GroupVisitorMapper groupVisitorMapper;

    public Map getMap(Integer groupOrderId, String... seqId) {
        GroupOrder groupOrder = groupOrderMapper.selectByPrimaryKey(groupOrderId);
        List<GroupVisitor> visitorList = groupVisitorMapper.selectByGroupSeqId(groupOrderId, seqId);
        Map map = getOrderMap(groupOrder);
        map.put("visitors", getVisitorMap(visitorList));
        map.put("supplyId", groupOrder.getSupply_id());
        return map;
    }

    private Map getOrderMap(GroupOrder groupOrder) {
        Map map = new HashMap();
        map.put("voucherId", groupOrder.getOrderCode());
        map.put("guideName", groupOrder.getGuideName());
        map.put("guideIdNumber", groupOrder.getGuideIdNumber());
        map.put("guideMobile", groupOrder.getGuideMobile());
        map.put("number", groupOrder.getNumber());
        return map;
    }

    private List<Map> getVisitorMap(List<GroupVisitor> visitorList) {
        List<Map> mapList = new ArrayList<>();
        for (GroupVisitor visitor : visitorList) {
            Map map = new HashMap();

            map.put("customName", visitor.getCustomName());
            map.put("mobile", visitor.getMobile());
            map.put("idType", visitor.getIdType());
            map.put("idNumber", visitor.getIdNumber());
            map.put("seqId", visitor.getSeqId());

            mapList.add(map);
        }
        return mapList;
    }
}
