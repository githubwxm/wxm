package com.all580.voucherplatform.adapter.supply.ticketV3.manager;

import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.GroupVisitorMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.GroupVisitor;
import com.all580.voucherplatform.entity.SupplyProduct;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-13.
 */
@Service
@Slf4j
public class GroupOrderManager {
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    private GroupVisitorMapper groupVisitorMapper;
    @Autowired
    public SupplyProductMapper supplyProductMapper;

    public GroupOrderManager() {}

    public Map getMap(int groupId) throws Exception {
        GroupOrder groupOrder = groupOrderMapper.selectByPrimaryKey(groupId);
        if (groupOrder == null) {
            throw new Exception("订单不存在");
        }
        if (groupOrder.getSupplyProdId() == null) {
            log.debug("订单未绑定票务产品");
            throw new Exception("订单未绑定票务产品");
        }
        Map map = new HashMap();
        map.put("voucherId", groupOrder.getOrderCode());
        map.put("otaOrderId", groupOrder.getPlatformOrderId());
        map.put("formAreaCode", groupOrder.getFormAreaCode());
        map.put("formAddr", StringUtils.isEmpty(groupOrder.getFormAddr())?groupOrder.getFormAreaCode():groupOrder.getFormAddr());
        map.put("travelName", groupOrder.getTravelName());
        map.put("manager", StringUtils.isEmpty(groupOrder.getManager())?"经理人":groupOrder.getManager()  );
        map.put("groupNumber", groupOrder.getGroupNumber());
        map.put("guideName", groupOrder.getGuideName());
        map.put("guideIdNumber", groupOrder.getGuideIdNumber());
        map.put("guideMobile", groupOrder.getGuideMobile());
        map.put("payment", groupOrder.getPayment());
        map.put("payTime", groupOrder.getPayTime() == null ? null : DateFormatUtils.converToStringTime(groupOrder.getPayTime()));
        map.put("qrCode", groupOrder.getVoucherNumber());
        map.put("validTime", DateFormatUtils.converToStringTime(groupOrder.getValidTime()));
        map.put("invalidTime", DateFormatUtils.converToStringTime(groupOrder.getInvalidTime()));
        map.put("products", getProdMap(groupOrder));
        map.put("visitors", getVisitorMap(groupOrder));
        map.put("supplyId", groupOrder.getSupply_id());
        map.put("channel",groupOrder.getChannel());
        map.put("number",groupOrder.getNumber());
        return map;
    }

    private Map getProdMap(GroupOrder groupOrder) throws Exception {
        Map map = new HashMap();
        SupplyProduct supplyProduct = supplyProductMapper.selectByPrimaryKey(groupOrder.getSupplyProdId());
        map.put("productId", groupOrder.getSupplyProdId());
        map.put("ticketId", supplyProduct.getCode());
        map.put("price", groupOrder.getPrice());
        map.put("number", groupOrder.getNumber());
        return map;
    }

    private List<Map> getVisitorMap(GroupOrder groupOrder) {
        List<GroupVisitor> visitorList = groupVisitorMapper.selectByGroupOrderId(groupOrder.getId());
        List<Map> mapList = new ArrayList<>();
        for (GroupVisitor visitor : visitorList) {
            Map map = new HashMap();
            map.put("customName", visitor.getCustomName());
            map.put("mobile", visitor.getMobile());
            map.put("idType", visitor.getId());
            map.put("idNumber", visitor.getIdNumber());
            map.put("seqId", visitor.getSeqId());
            mapList.add(map);
        }
        return mapList;
    }
}
