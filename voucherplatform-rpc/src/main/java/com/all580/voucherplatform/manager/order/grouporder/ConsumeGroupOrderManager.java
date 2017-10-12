package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.GroupVisitorMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;

/**
 * Created by Linv2 on 2017-06-14.
 */
@Component
@Slf4j
public class ConsumeGroupOrderManager {
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    private GroupVisitorMapper groupVisitorMapper;

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void consume(Integer number, GroupOrder groupOrder, String... IdNumbers) throws ApiException {
        Integer totalNumber = groupOrder.getTotalNumber();//已经核销的数量
//        if (groupOrder.getActivateStatus()) {// 核销多次不适用
//            throw new Exception("订单状态已激活");
//        }
        if (totalNumber + number > groupOrder.getNumber()) {
            throw new ApiException("没有足够核销的票");
        }
        GroupOrder updateGroupOrder = new GroupOrder();
        updateGroupOrder.setId(groupOrder.getId());
        updateGroupOrder.setActivateStatus(true);
        updateGroupOrder.setActivateNum(number);
        updateGroupOrder.setTotalNumber(number + totalNumber);
        groupOrderMapper.updateByPrimaryKeySelective(updateGroupOrder);
        groupVisitorMapper.updateActivateByIdNumber(groupOrder.getId(), IdNumbers);
    }
}
