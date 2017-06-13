package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.GroupVisitorMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.GroupVisitor;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-12.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class UpdateGroupOrderManager {
    @Autowired
    public GroupOrderMapper groupOrderMapper;
    @Autowired
    public GroupVisitorMapper groupVisitorMapper;

    private GroupOrder order;
    private GroupOrder updateOrder;
    private List<GroupVisitor> visitorList;

    public void setOrder(Integer platformId, String platformOrderCode) {
        this.order = groupOrderMapper.selectByPlatform(platformId, platformOrderCode);
    }

    public void setData(Map map) {
        this.updateOrder = new GroupOrder();
        this.visitorList = new ArrayList<>();
        updateOrder.setId(this.order.getId());
        updateOrder.setGuideName(CommonUtil.objectParseString("guideName"));
        updateOrder.setGuideMobile(CommonUtil.objectParseString("guideIdNumber"));
        updateOrder.setGroupNumber(CommonUtil.objectParseString("guideMobile"));
        List<Map> visitorList = (List<Map>) map.get("visitors");
        setVisitor(visitorList);
    }

    private void setVisitor(List<Map> visitorList) {

        for (Map mapVisitor : visitorList) {
            GroupVisitor groupVisitor = new GroupVisitor();
            groupVisitor.setGroup_order_id(order.getId());
            groupVisitor.setSeqId(CommonUtil.objectParseString(mapVisitor.get("seqId")));
            groupVisitor.setCustomName(CommonUtil.objectParseString(mapVisitor.get("customName")));
            groupVisitor.setMobile(CommonUtil.objectParseString(mapVisitor.get("mobile")));
            groupVisitor.setIdType(CommonUtil.objectParseString(mapVisitor.get("idType")));
            groupVisitor.setIdNumber(CommonUtil.objectParseString(mapVisitor.get("idNumber")));
            this.visitorList.add(groupVisitor);
        }
    }

    public void saveOrder() {
        groupOrderMapper.updateByPrimaryKeySelective(updateOrder);
        for (GroupVisitor visitor : visitorList) {
            groupVisitorMapper.updateByVisitorSeqId(visitor);
        }
    }
}
