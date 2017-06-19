package com.all580.voucherplatform.manager.order.grouporder;

import com.all580.voucherplatform.adapter.AdapterLoadder;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.utils.sign.async.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Linv2 on 2017-06-16.
 */
@Component
@Slf4j
public class UpdateResultGroupOrderManager {
    @Autowired
    private GroupOrderMapper groupOrderMapper;

    @Autowired
    private AdapterLoadder adapterLoadder;
    @Autowired
    private AsyncService asyncService;

    public void success(String orderCode) {
        GroupOrder groupOrder = groupOrderMapper.selectByOrderCode(orderCode);
        notifyPlatform(groupOrder.getPlatform_id(), groupOrder.getId());
    }

    public void notifyPlatform(final Integer platformId, final Integer groupOrderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoadder.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.updateGroup(groupOrderId);
                }
            }
        });
    }
}
