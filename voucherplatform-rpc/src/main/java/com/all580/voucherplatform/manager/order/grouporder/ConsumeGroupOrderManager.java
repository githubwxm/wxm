package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.GroupVisitorMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Linv2 on 2017-06-14.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class ConsumeGroupOrderManager {
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    private GroupVisitorMapper groupVisitorMapper;
    private GroupOrder groupOrder;

    public void setOrder(String orderCode) {
        this.groupOrder = groupOrderMapper.selectByOrderCode(orderCode);
    }

    public void Consume(Integer number, String... IdNumbers) throws Exception {
        if (groupOrder.getActivateStatus()) {
            throw new Exception("订单状态已激活");
        }
        GroupOrder updateGroupOrder = new GroupOrder();
        updateGroupOrder.setId(groupOrder.getId());
        updateGroupOrder.setActivateStatus(true);
        updateGroupOrder.setActivateNum(number);
        groupOrderMapper.updateByPrimaryKeySelective(updateGroupOrder);
        groupVisitorMapper.updateActivateByIdNumber(groupOrder.getId(), IdNumbers);

    }
}
