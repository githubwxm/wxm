package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.GroupVisitorMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.utils.async.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;
    private GroupOrder groupOrder;

    public void setOrder(String orderCode) {
        this.groupOrder = groupOrderMapper.selectByOrderCode(orderCode);
    }


    public void submiConsume(Integer number, String... IdNumbers) throws Exception {
        Consume(number, IdNumbers);
        notifyPlatform(groupOrder.getPlatform_id(), groupOrder.getId());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private void Consume(Integer number, String... IdNumbers) throws Exception {
        Integer  totalNumber=  groupOrder.getTotalNumber();//已经核销的数量
//        if (groupOrder.getActivateStatus()) {// 核销多次不适用
//            throw new Exception("订单状态已激活");
//        }
        if(totalNumber+number>groupOrder.getNumber()){
            throw new Exception("没有足够核销的票");
        }
        GroupOrder updateGroupOrder = new GroupOrder();
        updateGroupOrder.setId(groupOrder.getId());
        updateGroupOrder.setActivateStatus(true);
        updateGroupOrder.setActivateNum(number);
        updateGroupOrder.setTotalNumber(number+totalNumber);
        groupOrderMapper.updateByPrimaryKeySelective(updateGroupOrder);
        groupVisitorMapper.updateActivateByIdNumber(groupOrder.getId(), IdNumbers);

    }


    private void notifyPlatform(final Integer platformId, final Integer groupOrderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.activateGroupOrder(groupOrderId);
                }
            }
        });
    }
}
